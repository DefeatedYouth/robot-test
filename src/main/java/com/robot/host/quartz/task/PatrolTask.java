package com.robot.host.quartz.task;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.robot.host.base.entry.*;
import com.robot.host.base.service.*;
import com.robot.host.common.constants.*;
import com.robot.host.common.dto.MessageAboutRobotDTO;
import com.robot.host.common.dto.PatrolRouteDTO;
import com.robot.host.common.dto.PatrolTaskResultDTO;
import com.robot.host.common.dto.PatrolTaskStatusDTO;
import com.robot.host.common.util.FTPRobotUtils;
import com.robot.host.common.util.SysConfigUtil;
import com.robot.host.netty.resolver.out.CommonOutResolver;
import com.robot.host.quartz.constants.JobConstants;
import com.robot.host.common.util.MessageUtil;
import com.robot.host.quartz.entry.QuartzTriggers;
import com.robot.host.quartz.service.QuartzTriggersService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.testng.collections.Lists;

import java.io.InputStream;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component("patrolTask")
@Slf4j
public class PatrolTask implements ITask {
    /**
     * 巡视任务状态
     */
    public static final Integer EXECUTED = 1;
    public static final Integer RUNNING = 2;
    public static final Integer PAUSE = 3;
    public static final Integer TERMINATION = 4;
    public static final Integer NONEXECUTED = 5;
    public static final Integer OVERDUE = 6;


    @Autowired
    private PatrolTaskService patrolTaskService;
    @Autowired
    private PatrolTaskExecService patrolTaskExecService;

    @Autowired
    private DeviceInfoService deviceInfoService;

    @Autowired
    private RobotInfoService robotInfoService;

    @Autowired
    private PatrolTaskResultService patrolTaskResultService;

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private QuartzTriggersService quartzTriggersService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private FTPRobotUtils ftpRobotUtils;

    @Override
    public void run(String params) {
        JSONObject paramObj = JSONUtil.parseObj(params);
        Long jobId = Long.valueOf((Integer) paramObj.get("jobId"));
        //获取机器人
        List<RobotInfoEntity> robotList = robotInfoService.list(new QueryWrapper<RobotInfoEntity>().lambda().eq(RobotInfoEntity::getStatus, 1));
        if(!robotList.isEmpty()){

            String robotCode = robotList.get(0).getCode();
            String robotName = robotList.get(0).getName();
            log.info("{}正在执行任务",robotName);

            //获取taskId
            JSONObject json = JSONUtil.parseObj(params);
            Long busiId = json.getLong(JobConstants.QUARTZ_BUSI_ID);
            String deviceList = json.getStr(JobConstants.QUARTZ_DEVICE_LIST);
            String deviceLevel = json.getStr(JobConstants.QUARTZ_DEVICE_LEVEL);
            Long execId = 0L;
            PatrolTaskEntity patrolTask = patrolTaskService.getById(busiId);
            //任务开始执行
            this.saveOperationLog(robotList.get(0).getRobotInfoId(), busiId, String.format(SysLogConstant.ROBOT_PATROL_TASK_START, "巡检任务", jobId), null);
            //设置任务实际执行时间
            patrolTask.setRealBeginTime(new Date(System.currentTimeMillis()));
            patrolTaskService.saveOrUpdate(patrolTask);
            //新增执行记录
            PatrolTaskExecEntity execEntity = new PatrolTaskExecEntity();
            execEntity.setExecStatus(patrolTask.getExecStatus());
            execEntity.setPatrolTaskCode(patrolTask.getPatrolTaskCode());
            execEntity.setPatrolRuleType(patrolTask.getPatrolRuleType());
            execEntity.setPatrolTaskId(patrolTask.getPatrolTaskId());
            execEntity.setPatrolType(patrolTask.getPatrolType());
            execEntity.setSiteId(patrolTask.getSiteId());
            execEntity.setCreateTime(new Date());
//        execEntity.setExecCommand(params);
            patrolTaskExecService.save(execEntity);

            execId = execEntity.getPatrolTaskExecId();
            //修改巡检任务状态
            this.updatePatrolTaskStatus(busiId,patrolTask,PatrolTask.RUNNING,"任务正在执行");
            //修改机器人状态
            this.updateRobotStatus(robotCode,2, EnumRobotComplusStatusDataType.YunXingZhuangTai_XunShi);
            //生成巡视路线
            this.createPatrolRoute(robotCode,deviceList,patrolTask);

            //获取任务场景
            List<DeviceInfoEntry> scenes = deviceInfoService.list(new QueryWrapper<DeviceInfoEntry>().lambda().in(DeviceInfoEntry::getDeviceId, Lists.newArrayList(deviceList.split(","))));
            scenes.forEach(scene -> {
                log.info("{}正在前往：{}",robotName,scene.getDeviceName());
//                robotInfoService.updateCoordinate(robotCode,scene.getPosX(), scene.getPosY());
                PatrolTaskResultEntity result = new PatrolTaskResultEntity();
                result.setRobotCode(robotCode);
                result.setTaskName(patrolTask.getPatrolTaskName());
                result.setTaskCode(patrolTask.getPatrolTaskCode());
                result.setDeviceName(scene.getDeviceName());
                result.setDeviceId(Long.valueOf(scene.getDeviceId()));
                //TODO  单位值
                result.setValue("");
                result.setUnit("");
                result.setTime(new Date());
                result.setRecognitionType(scene.getRecognitionTypeList());
                //TODO 文件
                result.setFileType(StringUtils.isBlank(scene.getSaveTypeList())? null : Integer.valueOf(scene.getSaveTypeList()));
                result.setFilePath(this.getResultFileName(scene.getDeviceId(), robotCode, patrolTask));
                result.setRectangle("");
                result.setTaskPatrolledId(execEntity.getPatrolTaskExecId());
                patrolTaskResultService.save(result);
                this.patrolResultData(result.getPatrolTaskResultId());
                //添加操作日志
                this.saveOperationLog(robotList.get(0).getRobotInfoId(),
                        busiId,
                        String.format(SysLogConstant.ROBOT_PATROL_TASK_ING, "巡检任务", robotCode, scene.getDeviceName()),
                        scene.getDeviceId());
                //巡检耗时
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });

            //修改巡检任务状态
            this.updatePatrolTaskStatus(busiId, patrolTask,PatrolTask.EXECUTED,"任务已执行");
            //修改机器人状态
            this.updateRobotStatus(robotCode,1, EnumRobotComplusStatusDataType.YunXingZhuangTai_KongXian);
            //任务结束，返回巡视结果
//            this.patrolResultData(execId);
            this.saveOperationLog(robotList.get(0).getRobotInfoId(), busiId, String.format(SysLogConstant.ROBOT_PATROL_TASK_START, "巡检任务", jobId), null);
        }else{
            //TODO
            log.info("当前没有空闲机器人");
        }
    }


    //上传结果文件
    private String getResultFileName(String deviceId, String robotCode, PatrolTaskEntity patrolTaskEntity) {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        String siteId = patrolTaskEntity.getSiteId();
        int year = cal.get(Calendar.YEAR);
        String month = cal.get(Calendar.MONTH) + 1 > 10 ? cal.get(Calendar.MONTH) + 1 + "" : "0" + (cal.get(Calendar.MONTH) + 1);
        String day = cal.get(Calendar.DAY_OF_MONTH) > 10 ? cal.get(Calendar.DAY_OF_MONTH) + "" : "0" + cal.get(Calendar.DAY_OF_MONTH);
        String taskcode = patrolTaskEntity.getPatrolTaskCode();
        //filePath: 变电站id/年/月/日/巡视任务编码/CCD
        String filePath = siteId + "/" + year + "/" + month + "/" + day + "/" + taskcode + "/CCD";
        String fileName = deviceId + "_" + robotCode + "_" + NettyConstants.fileDateFormat.format(date) + ".jpg";
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("banner.jpg");
        boolean flag = ftpRobotUtils.uploadFile(filePath, fileName, is);
        if(!flag){
            return "文件上传失败";
        }
        return filePath + "/" + fileName;
    }

    /**
     * 添加任务执行日志
     * @param robotId
     * @param busiId
     * @param logContent
     * @param deviceId
     */
    @Override
    public void saveOperationLog(Long robotId, Long busiId, String logContent, String deviceId) {
        OperationLogEntity log = new OperationLogEntity();
        log.setLogType(NettyConstants.PATROL_TASK);
        log.setRobotId(robotId);
        log.setJobId(busiId);
        log.setLogContent(logContent);
        log.setDeviceId(deviceId);
        log.setOperationTime(System.currentTimeMillis());
        operationLogService.save(log);
        //推送任务状态结果给前台
        messagingTemplate.convertAndSend(EnumTopicDistination.PATROL_TASK.getText(),busiId);
    }

    /**
     * 返回巡检结果
     * @param taskResultId
     */
    private void patrolResultData(Long taskResultId) {
        PatrolTaskResultDTO resultDTO = new PatrolTaskResultDTO();
        resultDTO.setTaskResultId(taskResultId);
        String resultMsg = JSONUtil.toJsonStr(resultDTO);
        MessageAboutRobotDTO message = new MessageAboutRobotDTO();
        message.setMsgBody(resultMsg);
        message.setMsgType(EnumSendToRobotMsgType.PATROL_TASK_RESULT);
        MessageUtil.sendMessage(JSONUtil.toJsonStr(message));
    }

    /**
     * 生成巡视路线
     * @param robotCode
     * @param deviceList
     * @param patrolTaskEntity
     */
    private void createPatrolRoute(String robotCode, String deviceList, PatrolTaskEntity patrolTaskEntity) {
        PatrolRouteDTO patrolRouteDTO = new PatrolRouteDTO();
        patrolRouteDTO.setRobotCode(robotCode);
        patrolRouteDTO.setSceneIds(deviceList);
        patrolRouteDTO.setPatrolTaskEntity(patrolTaskEntity);
        String patrolRouteMsg = JSONUtil.toJsonStr(patrolRouteDTO);
        MessageAboutRobotDTO messageAboutRobotDTO = new MessageAboutRobotDTO();
        messageAboutRobotDTO.setMsgBody(patrolRouteMsg);
        messageAboutRobotDTO.setMsgType(EnumSendToRobotMsgType.PATROL_ROUTE);
        MessageUtil.sendMessage(JSONUtil.toJsonStr(messageAboutRobotDTO));
    }

    /**
     * 修改机器人状态
     * @param robotCode
     * @param status
     * @param statusData
     */
    private void updateRobotStatus(String robotCode, Integer status, EnumRobotComplusStatusDataType statusData) {
        RobotInfoEntity robotInfo = robotInfoService.getOne(new QueryWrapper<RobotInfoEntity>().lambda().eq(RobotInfoEntity::getCode, robotCode));
        robotInfo.setStatus(status);
        robotInfoService.saveOrUpdate(robotInfo);

        MessageUtil.statusDataMessage(robotCode, statusData);
    }

    /**
     * 修改巡检任务状态
     * @param execId
     * @param patrolTask
     * @param state
     * @param description
     */
    private void updatePatrolTaskStatus(long execId, PatrolTaskEntity patrolTask, Integer state, String description) {
        patrolTask.setExecStatus(state);
        patrolTaskService.saveOrUpdate(patrolTask);

        //推送任务状态
        PatrolTaskStatusDTO taskStatusDTO = new PatrolTaskStatusDTO();
        taskStatusDTO.setTaskPatrolledId(execId);
        taskStatusDTO.setTaskCode(patrolTask.getPatrolTaskCode());
        taskStatusDTO.setDescription(description);
        String taskStatusMsg = JSONUtil.toJsonStr(taskStatusDTO);

        MessageAboutRobotDTO messageAboutRobotDTO = new MessageAboutRobotDTO();
        messageAboutRobotDTO.setMsgType(EnumSendToRobotMsgType.PATROL_TASK_STATUS);
        messageAboutRobotDTO.setMsgBody(taskStatusMsg);
        String jsonStr = JSONUtil.toJsonStr(messageAboutRobotDTO);
        MessageUtil.sendMessage(jsonStr);

    }


}
