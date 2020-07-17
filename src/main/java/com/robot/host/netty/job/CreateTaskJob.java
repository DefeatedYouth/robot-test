package com.robot.host.netty.job;

import cn.hutool.json.JSONObject;
import com.robot.host.common.constants.SmartConstants;
import com.robot.host.common.constants.ProtocolMessage;
import com.robot.host.common.dto.XmlOutRobotPatralTaskDownDTO;
import com.robot.host.base.entry.PatrolTaskExecEntity;
import com.robot.host.quartz.entry.ScheduleJobEntity;
import com.robot.host.quartz.service.ScheduleJobService;
import com.robot.host.quartz.util.CronDateUtils;
import com.robot.host.base.service.PatrolTaskExecService;
import com.robot.host.common.util.XmlBeanUtils;

public class CreateTaskJob implements Runnable {

    private ProtocolMessage protocolMessage;

    private PatrolTaskExecService patrolTaskExecService;

    private ScheduleJobService scheduleJobService;

    public CreateTaskJob(ProtocolMessage protocolMessage, PatrolTaskExecService patrolTaskExecService, ScheduleJobService scheduleJobService) {
        this.protocolMessage = protocolMessage;
        this.patrolTaskExecService = patrolTaskExecService;
        this.scheduleJobService = scheduleJobService;
    }

    @Override
    public void run() {
        XmlOutRobotPatralTaskDownDTO xmlOutRobotPatralTaskDownDTO = XmlBeanUtils.xmlToBean(protocolMessage.getBody(), XmlOutRobotPatralTaskDownDTO.class);
        XmlOutRobotPatralTaskDownDTO.Item item = xmlOutRobotPatralTaskDownDTO.getItems().get(0);
        //巡检任务执行情况
        PatrolTaskExecEntity patrolTaskExecEntity = new PatrolTaskExecEntity();
        patrolTaskExecEntity.setPatrolTaskCode(item.getTaskCode());
        patrolTaskExecEntity.setPatrolTaskName(item.getTaskName());
        patrolTaskExecEntity.setPatrolType(Integer.valueOf(item.getType()));
        patrolTaskExecService.save(patrolTaskExecEntity);
        Long patrolTaskExecId = patrolTaskExecEntity.getPatrolTaskExecId();

        //添加定时任务
        ScheduleJobEntity scheduleJobEntity = new ScheduleJobEntity();
        //获取cron表达式
        String cron = CronDateUtils.getCron(item.getFixedStartTime());
        scheduleJobEntity.setCronExpression(cron);

        scheduleJobEntity.setBeanName(SmartConstants.patrol_task_bean_name);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("exec_id",patrolTaskExecId);
        jsonObject.put("device_level",item.getDeviceLevel());
        jsonObject.put("device_list",item.getDeviceList());
        scheduleJobEntity.setParams(jsonObject.toString());

        scheduleJobService.saveJob(scheduleJobEntity);


    }
}
