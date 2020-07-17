package com.robot.host.quartz.task;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.robot.host.base.entry.*;
import com.robot.host.base.service.*;
import com.robot.host.common.constants.EnumRobotComplusStatusDataType;
import com.robot.host.common.constants.EnumSendToRobotMsgType;
import com.robot.host.common.dto.MessageAboutRobotDTO;
import com.robot.host.common.dto.PatrolRouteDTO;
import com.robot.host.common.dto.PatrolTaskStatusDTO;
import com.robot.host.quartz.constants.JobConstants;
import com.robot.host.common.util.MessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    @Override
    public void run(String params) {
        //获取机器人
        List<RobotInfoEntity> robotList = robotInfoService.list(new QueryWrapper<RobotInfoEntity>().lambda().eq(RobotInfoEntity::getStatus, 1));
        if(!robotList.isEmpty()){

            String robotCode = robotList.get(0).getCode();
            String robotName = robotList.get(0).getName();
            log.info("{}正在执行任务",robotName);

            //获取taskId
            JSONObject json = JSONUtil.parseObj(params);
            long busiId = json.getLong(JobConstants.QUARTZ_BUSI_ID);
            String deviceList = json.getStr(JobConstants.QUARTZ_DEVICE_LIST);
            String deviceLevel = json.getStr(JobConstants.QUARTZ_DEVICE_LEVEL);
            long execId = 0;
            PatrolTaskEntity patrolTask = patrolTaskService.getById(busiId);
            //修改巡检任务状态
            this.updatePatrolTaskStatus(patrolTask,PatrolTask.RUNNING,"任务正在执行");
            //修改机器人状态
            this.updateRobotStatus(robotCode,2, EnumRobotComplusStatusDataType.YunXingZhuangTai_XunShi);
            //生成巡视路线
            this.createPatrolRoute(robotCode,deviceList);
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

            //获取任务场景
            List<DeviceInfoEntry> scenes = deviceInfoService.list(new QueryWrapper<DeviceInfoEntry>().lambda().in(DeviceInfoEntry::getDeviceId, deviceList));
            scenes.forEach(scene -> {
                log.info("{}正在前往：{}",robotName,scene.getDeviceName());
                robotInfoService.updateCoordinate(robotCode,scene.getPosX(), scene.getPosY());
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
                result.setRecognitionType(Integer.valueOf(scene.getRecognitionTypeList()));
                //TODO 文件
                result.setFileType(Integer.valueOf(scene.getSaveTypeList()));
                result.setFilePath("");
                result.setRectangle("");

                result.setPatrolTaskResultId(execEntity.getPatrolTaskExecId());

                patrolTaskResultService.save(result);

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });

            //修改巡检任务状态
            this.updatePatrolTaskStatus(patrolTask,PatrolTask.EXECUTED,"任务已执行");
            //修改机器人状态
            this.updateRobotStatus(robotCode,1, EnumRobotComplusStatusDataType.YunXingZhuangTai_KongXian);
        }else{
            log.info("当前没有空闲机器人");
        }
    }

    /**
     * 生成巡视沦陷
     * @param robotCode
     * @param deviceList
     */
    private void createPatrolRoute(String robotCode, String deviceList) {
        PatrolRouteDTO patrolRouteDTO = new PatrolRouteDTO();
        patrolRouteDTO.setRobotCode(robotCode);
        patrolRouteDTO.setSceneIds(deviceList);
        String patrolRouteMsg = JSONUtil.toJsonStr(patrolRouteDTO);
        MessageAboutRobotDTO messageAboutRobotDTO = new MessageAboutRobotDTO();
        messageAboutRobotDTO.setMsgBody(patrolRouteMsg);
        messageAboutRobotDTO.setMsgType(EnumSendToRobotMsgType.PATROL_ROUTE);
        MessageUtil.sendMessage(JSONUtil.toJsonStr(messageAboutRobotDTO));
    }

    /**
     * 修改机器人状态
     * @param robotCode
     * @param i
     * @param yunXingZhuangTai_kongXian
     */
    private void updateRobotStatus(String robotCode, Integer status, EnumRobotComplusStatusDataType statusData) {
        RobotInfoEntity robotInfo = robotInfoService.getOne(new QueryWrapper<RobotInfoEntity>().lambda().eq(RobotInfoEntity::getCode, robotCode));
        robotInfo.setStatus(status);
        robotInfoService.saveOrUpdate(robotInfo);

        MessageUtil.statusDataMessage(robotCode, statusData,null,null);
    }

    /**
     * 修改巡检任务状态
     * @param patrolTask
     * @param state
     * @param description
     */
    private void updatePatrolTaskStatus(PatrolTaskEntity patrolTask, Integer state,String description) {
        patrolTask.setExecStatus(state);
        patrolTaskService.saveOrUpdate(patrolTask);

        //推送任务状态
        PatrolTaskStatusDTO taskStatusDTO = new PatrolTaskStatusDTO();
        taskStatusDTO.setTaskCode(patrolTask.getPatrolTaskCode());
        taskStatusDTO.setDescription(description);
        String taskStatusMsg = JSONUtil.toJsonStr(taskStatusDTO);
        MessageUtil.sendMessage(taskStatusMsg);

    }


}
