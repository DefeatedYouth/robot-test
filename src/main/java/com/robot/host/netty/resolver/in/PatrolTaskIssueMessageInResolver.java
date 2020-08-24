package com.robot.host.netty.resolver.in;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import com.robot.host.base.service.OperationLogService;
import com.robot.host.common.constants.*;
import com.robot.host.common.dto.BaseXmlDTO;
import com.robot.host.common.dto.MessageJudgeInDTO;
import com.robot.host.common.dto.PatrolRouteDTO;
import com.robot.host.common.dto.XmlOutRobotPatralTaskDownDTO;
import com.robot.host.base.entry.PatrolTaskEntity;
import com.robot.host.quartz.constants.JobConstants;
import com.robot.host.quartz.entry.ScheduleJobEntity;
import com.robot.host.quartz.service.ScheduleJobService;
import com.robot.host.base.service.PatrolTaskService;
import com.robot.host.common.util.QuartzUtil;
import com.robot.host.common.util.XmlBeanUtils;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.transaction.annotation.Transactional;
import org.testng.collections.Lists;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class PatrolTaskIssueMessageInResolver implements InResolver {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private PatrolTaskService patrolTaskService;

    private ScheduleJobService scheduleJobService;

    private OperationLogService operationLogService;

    public PatrolTaskIssueMessageInResolver(PatrolTaskService patrolTaskService, ScheduleJobService scheduleJobService, OperationLogService operationLogService) {
        this.patrolTaskService = patrolTaskService;
        this.scheduleJobService = scheduleJobService;
        this.operationLogService = operationLogService;
    }

    @Override
    public boolean support(MessageJudgeInDTO judgeInDTO) {
        if (!StringUtils.equals(judgeInDTO.getType(), NettyConstants.OUT_CODE_TASK_DOWN + "")) {
            return false;
        }
        if (!StringUtils.equals(judgeInDTO.getCommand(), NettyConstants.OUT_CODE_TASK_DOWN_COMMAND + "")) {
            return false;
        }
        return true;
    }

    @Override
    public ProtocolMessage resolve(ProtocolMessage message, ChannelHandlerContext ctx){
        ProtocolMessage protocol = new ProtocolMessage();
        boolean flag = false;
        try {
            XmlOutRobotPatralTaskDownDTO xmlOutRobotPatralTaskDownDTO = XmlBeanUtils.xmlToBean(message.getBody(), XmlOutRobotPatralTaskDownDTO.class);
            XmlOutRobotPatralTaskDownDTO.Item item = xmlOutRobotPatralTaskDownDTO.getItems().get(0);
            //巡检任务执行情况
            PatrolTaskEntity patrolTaskEntity = new PatrolTaskEntity();
            patrolTaskEntity.setPatrolType(Integer.valueOf(item.getType()));
            patrolTaskEntity.setPatrolTaskCode(item.getTaskCode());
            patrolTaskEntity.setPatrolTaskName(item.getTaskName());
            patrolTaskEntity.setPriority(Integer.valueOf(item.getPriority()));
            patrolTaskEntity.setSiteId(xmlOutRobotPatralTaskDownDTO.getCode());

            String fixedStartTime = item.getFixedStartTime();
            String cycleMonth = item.getCycleMonth();
            String cycleWeek = item.getCycleWeek();
            String intervalNumber = item.getIntervalNumber();
            if(StringUtils.isNotBlank(fixedStartTime)){//       定时任务
                flag = true;
                patrolTaskEntity.setFixedTime(fixedStartTime);
                patrolTaskEntity.setPatrolRuleType(2);
                patrolTaskEntity.setPeriodType(EnumTaskPeriodType.TIMEDTASK.getValue());
            }else if(StringUtils.isNotBlank(cycleMonth)){//     月
                patrolTaskEntity.setPatrolRuleType(3);
                patrolTaskEntity.setTaskBeginTime(sdf.parse(item.getCycleStartTime()));
                patrolTaskEntity.setTaskEndTime(sdf.parse(item.getCycleEndTime()));
                patrolTaskEntity.setPeriodType(EnumTaskPeriodType.MONTH.getValue());
                patrolTaskEntity.setPeriodVal(item.getCycleMonth());
                patrolTaskEntity.setPeriodExecuteTime(item.getCycleExecuteTime());
            }else if(StringUtils.isNotBlank(cycleWeek)){//      周
                patrolTaskEntity.setPatrolRuleType(3);
                patrolTaskEntity.setTaskBeginTime(sdf.parse(item.getCycleStartTime()));
                patrolTaskEntity.setTaskEndTime(sdf.parse(item.getCycleEndTime()));
                patrolTaskEntity.setPeriodType(EnumTaskPeriodType.WEEK.getValue());
                patrolTaskEntity.setPeriodVal(item.getCycleWeek());
                patrolTaskEntity.setPeriodExecuteTime(item.getCycleExecuteTime());
            }else if(StringUtils.isNotBlank(intervalNumber)){//     天、小时
                patrolTaskEntity.setPatrolRuleType(3);
                patrolTaskEntity.setTaskBeginTime(sdf.parse(item.getIntervalStartTime()));
                patrolTaskEntity.setTaskEndTime(sdf.parse(item.getIntervalEndTime()));
                patrolTaskEntity.setPeriodType(Integer.valueOf(item.getIntervalType()));
                patrolTaskEntity.setPeriodVal(intervalNumber);
                patrolTaskEntity.setPeriodExecuteTime(item.getIntervalExecuteTime());
            }else{
                //TODO  立即执行任务
                flag = true;
                patrolTaskEntity.setPatrolRuleType(1);
                patrolTaskEntity.setFixedTime(DateUtil.format(DateUtils.addSeconds(new Date(), 10), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")));
                patrolTaskEntity.setPeriodType(EnumTaskPeriodType.EXEC.getValue());
            }
            //添加任务表
            patrolTaskService.save(patrolTaskEntity);

            //TODO 添加任务    将周期任务拆分成单个子任务
            this.insertBatchSchedule(flag, patrolTaskEntity, item);

            //添加任务表
//            patrolTaskService.save(patrolTaskEntity);

            //成功响应
            BaseXmlDTO taskIssueDTO = new BaseXmlDTO();
            taskIssueDTO.setSendCode(NettyConstants.ROBOT_HOST_CODE);
            taskIssueDTO.setReceiveCode(NettyConstants.PATROL_HOST_CODE);
            taskIssueDTO.setType(NettyConstants.IN_OUT_CODE_SYSTEM_TYPE + "");
            taskIssueDTO.setCommand(NettyConstants.OUT_CODE_SYSTEM_TYPE_COMMAND_COMMON_NO_ITEMS + "");
            taskIssueDTO.setCode(NettyConstants.RESPONSE_CODE_SUCCESS + "");
            String taskIssueMsg = XmlBeanUtils.beanToXml(taskIssueDTO, BaseXmlDTO.class);
            protocol.setBody(taskIssueMsg);
        } catch (Exception e) {
            //失败响应
            BaseXmlDTO taskIssueDTO = new BaseXmlDTO();
            taskIssueDTO.setSendCode(NettyConstants.ROBOT_HOST_CODE);
            taskIssueDTO.setReceiveCode(NettyConstants.PATROL_HOST_CODE);
            taskIssueDTO.setType(NettyConstants.IN_OUT_CODE_SYSTEM_TYPE + "");
            taskIssueDTO.setCommand(NettyConstants.OUT_CODE_SYSTEM_TYPE_COMMAND_COMMON_NO_ITEMS + "");
            taskIssueDTO.setCode(NettyConstants.RESPONSE_CODE_ERROR + "");
            String taskIssueMsg = XmlBeanUtils.beanToXml(taskIssueDTO, BaseXmlDTO.class);
            protocol.setBody(taskIssueMsg);
            e.printStackTrace();
        }

        return protocol;
    }

    /**
     * 添加任务
     * @param flag   true 定时任务    false  周期任务
     * @param patrolTaskEntity
     * @param item
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertBatchSchedule(boolean flag, PatrolTaskEntity patrolTaskEntity, XmlOutRobotPatralTaskDownDTO.Item item) {
        try {
            if(flag){//定时任务、立即执行任务
                ScheduleJobEntity scheduleJobEntity = new ScheduleJobEntity();
                //获取cron表达式
                StringBuffer result = new StringBuffer();
                String cron = QuartzUtil.getCronExpression(result, patrolTaskEntity.getPeriodType(), patrolTaskEntity.getPriority() + "", patrolTaskEntity.getPeriodExecuteTime(), patrolTaskEntity.getFixedTime());
                scheduleJobEntity.setCronExpression(cron);

                scheduleJobEntity.setBeanName(SmartConstants.patrol_task_bean_name);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put(JobConstants.QUARTZ_BUSI_ID,patrolTaskEntity.getPatrolTaskId());
                jsonObject.put(JobConstants.QUARTZ_DEVICE_LIST,item.getDeviceList());
                jsonObject.put(JobConstants.QUARTZ_DEVICE_LEVEL,item.getDeviceLevel());
                scheduleJobEntity.setParams(jsonObject.toString());

                scheduleJobService.saveJob(scheduleJobEntity);

                this.saveLog(String.format("[%s]创建巡检任务，任务id为：%s", operationName(), scheduleJobEntity.getJobId()));
            }else{//周期任务
                Date beginTime = patrolTaskEntity.getTaskBeginTime();
                Date endTime = patrolTaskEntity.getTaskEndTime();
                int timeDifference = this.getTimeDifference(beginTime,endTime);
                List<Integer> intervals = Lists.newArrayList(patrolTaskEntity.getPeriodVal().split(",")).stream().map(Integer::valueOf).collect(Collectors.toList());
                for (int i = 1; i <= timeDifference; i++) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd " + patrolTaskEntity.getPeriodExecuteTime());
                    String execTime = DateUtil.format(DateUtils.addDays(beginTime, i), sdf);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(sdf.parse(execTime));
                    //周期任务  当任务周期小于当前时间则不创建
                    if(!calendar.before(new Date())){
                        continue;
                    }
                    Integer isFlag = 0;
                    if(patrolTaskEntity.getPeriodType().equals(EnumTaskPeriodType.MONTH.getValue())){
                        isFlag = calendar.get(Calendar.DAY_OF_MONTH);
                    }else if(patrolTaskEntity.getPeriodType().equals(EnumTaskPeriodType.WEEK.getValue())){
                        isFlag = calendar.get(Calendar.DAY_OF_WEEK) == 1 ? 7 : calendar.get(Calendar.DAY_OF_WEEK) - 1;
                    }
                    //不在周期内，直接跳过
                    if(isFlag != 0 && !intervals.contains(isFlag)){
                        continue;
                    }
                    //周期内、添加scheduleJob
                    ScheduleJobEntity scheduleJobEntity = new ScheduleJobEntity();
                    //获取cron表达式
                    StringBuffer result = new StringBuffer();
                    String cron = QuartzUtil.getCronExpression(result, EnumTaskPeriodType.TIMEDTASK.getValue(), null, null, execTime);
                    scheduleJobEntity.setCronExpression(cron);
                    scheduleJobEntity.setBeanName(SmartConstants.patrol_task_bean_name);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(JobConstants.QUARTZ_BUSI_ID,patrolTaskEntity.getPatrolTaskId());
                    jsonObject.put(JobConstants.QUARTZ_DEVICE_LIST,item.getDeviceList());
                    jsonObject.put(JobConstants.QUARTZ_DEVICE_LEVEL,item.getDeviceLevel());
                    scheduleJobEntity.setParams(jsonObject.toString());
                    scheduleJobService.saveJob(scheduleJobEntity);
                    this.saveLog(String.format("[%s]创建巡检任务，任务id为：%s", operationName(), scheduleJobEntity.getJobId()));
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取日期差
     * @param beginTime
     * @param endTime
     * @return
     */
    public int getTimeDifference(Date beginTime, Date endTime) {
        int day = 1000 * 60 * 60 * 24;
//        Calendar startCal = Calendar.getInstance();
//        Calendar endCal = Calendar.getInstance();
//        startCal.setTime(beginTime);
//        endCal.setTime(endTime);
//        LocalDate beginDate = LocalDate.of(startCal.get(Calendar.YEAR), startCal.get(Calendar.MONTH), startCal.get(Calendar.DATE));
//        LocalDate endDate = LocalDate.of(endCal.get(Calendar.YEAR), endCal.get(Calendar.MONTH), endCal.get(Calendar.DATE));
//        Period between = Period.between(beginDate, endDate);
        long times = (endTime.getTime() - beginTime.getTime()) / day;
        return Integer.valueOf(times + "");
    }

    /**
     * 添加系统日志
     * @param logContent
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveLog(String logContent){
        operationLogService.saveSysLogThenSendWebSocket(SysLogConstant.ROBOT_OTHER,
                SysLogConstant.SYS_LOCAL_STATUS,
                logContent,
                null, null, null, className());
    }

    @Override
    public String operationName() {
        return "任务下发";
    }

    @Override
    public String className() {
        return this.getClass().getCanonicalName();
    }
}
