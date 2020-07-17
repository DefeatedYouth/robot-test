package com.robot.host.netty.resolver.in;

import cn.hutool.json.JSONObject;
import com.robot.host.common.constants.EnumTaskPeriodType;
import com.robot.host.common.constants.NettyConstants;
import com.robot.host.common.constants.ProtocolMessage;
import com.robot.host.common.constants.SmartConstants;
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

import java.text.SimpleDateFormat;

@Slf4j
public class PatrolTaskIssueMessageInResolver implements InResolver {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private PatrolTaskService patrolTaskService;

    private ScheduleJobService scheduleJobService;

    public PatrolTaskIssueMessageInResolver(PatrolTaskService patrolTaskService, ScheduleJobService scheduleJobService) {
        this.patrolTaskService = patrolTaskService;
        this.scheduleJobService = scheduleJobService;
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
        try {
            XmlOutRobotPatralTaskDownDTO xmlOutRobotPatralTaskDownDTO = XmlBeanUtils.xmlToBean(message.getBody(), XmlOutRobotPatralTaskDownDTO.class);
            XmlOutRobotPatralTaskDownDTO.Item item = xmlOutRobotPatralTaskDownDTO.getItems().get(0);
            //巡检任务执行情况
            PatrolTaskEntity patrolTaskEntity = new PatrolTaskEntity();
            patrolTaskEntity.setPatrolType(Integer.valueOf(item.getType()));
            patrolTaskEntity.setPatrolTaskCode(item.getTaskCode());
            patrolTaskEntity.setPatrolTaskName(item.getTaskName());
            patrolTaskEntity.setPriority(Integer.valueOf(item.getPriority()));

            String fixedStartTime = item.getFixedStartTime();
            String cycleMonth = item.getCycleMonth();
            String cycleWeek = item.getCycleWeek();
            String intervalNumber = item.getIntervalNumber();
            if(StringUtils.isNotBlank(fixedStartTime)){//       定时任务
                patrolTaskEntity.setFixedTime(fixedStartTime);
                patrolTaskEntity.setPeriodType(EnumTaskPeriodType.TIMEDTASK.getValue());
            }else if(StringUtils.isNotBlank(cycleMonth)){//     月
                patrolTaskEntity.setTaskBeginTime(sdf.parse(item.getCycleStartTime()));
                patrolTaskEntity.setTaskEndTime(sdf.parse(item.getCycleEndTime()));
                patrolTaskEntity.setPeriodType(EnumTaskPeriodType.MONTH.getValue());
                patrolTaskEntity.setPeriodVal(item.getCycleMonth());
                patrolTaskEntity.setPeriodExecuteTime(item.getCycleExecuteTime());
            }else if(StringUtils.isNotBlank(cycleWeek)){//      周
                patrolTaskEntity.setTaskBeginTime(sdf.parse(item.getCycleStartTime()));
                patrolTaskEntity.setTaskEndTime(sdf.parse(item.getCycleEndTime()));
                patrolTaskEntity.setPeriodType(EnumTaskPeriodType.WEEK.getValue());
                patrolTaskEntity.setPeriodVal(item.getCycleWeek());
                patrolTaskEntity.setPeriodExecuteTime(item.getCycleExecuteTime());
            }else if(StringUtils.isNotBlank(intervalNumber)){//     天、小时
                patrolTaskEntity.setTaskBeginTime(sdf.parse(item.getIntervalStartTime()));
                patrolTaskEntity.setTaskEndTime(sdf.parse(item.getIntervalEndTime()));
                patrolTaskEntity.setPeriodType(Integer.valueOf(item.getIntervalType()));
                patrolTaskEntity.setPeriodVal(intervalNumber);
                patrolTaskEntity.setPeriodExecuteTime(item.getIntervalExecuteTime());
            }
            //添加任务表
            patrolTaskService.save(patrolTaskEntity);

            //添加定时任务
            ScheduleJobEntity scheduleJobEntity = new ScheduleJobEntity();
            //获取cron表达式
            StringBuffer result = new StringBuffer();
            String cron = QuartzUtil.getCronExpression(result,patrolTaskEntity.getPeriodType(), patrolTaskEntity.getPeriodVal(), patrolTaskEntity.getPeriodExecuteTime(), patrolTaskEntity.getFixedTime());
            scheduleJobEntity.setCronExpression(cron);

            scheduleJobEntity.setBeanName(SmartConstants.patrol_task_bean_name);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put(JobConstants.QUARTZ_BUSI_ID,patrolTaskEntity.getPatrolTaskId());
            jsonObject.put(JobConstants.QUARTZ_DEVICE_LIST,item.getDeviceLevel());
            jsonObject.put(JobConstants.QUARTZ_DEVICE_LEVEL,item.getDeviceList());
            scheduleJobEntity.setParams(jsonObject.toString());

            scheduleJobService.saveJob(scheduleJobEntity);

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
}
