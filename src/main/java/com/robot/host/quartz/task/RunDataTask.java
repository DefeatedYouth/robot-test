package com.robot.host.quartz.task;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.robot.host.base.entry.DeviceInfoEntry;
import com.robot.host.base.entry.OperationLogEntity;
import com.robot.host.base.service.OperationLogService;
import com.robot.host.common.constants.EnumSendToRobotMsgType;
import com.robot.host.common.constants.EnumTopicDistination;
import com.robot.host.common.constants.NettyConstants;
import com.robot.host.common.constants.SysLogConstant;
import com.robot.host.common.dto.MessageAboutRobotDTO;
import com.robot.host.common.dto.XmlOutRobotPushCommonDTO;
import com.robot.host.base.entry.RobotInfoEntity;
import com.robot.host.netty.resolver.out.CommonOutResolver;
import com.robot.host.base.service.RobotInfoService;
import com.robot.host.common.util.MessageUtil;
import com.robot.host.common.util.XmlBeanUtils;
import com.robot.host.quartz.entry.QuartzTriggers;
import com.robot.host.quartz.service.QuartzTriggersService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.testng.collections.Lists;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Slf4j
@Component("runDataTask")
public class RunDataTask implements ITask {
    /**
     * 单位
     * speed
     * mileage
     * quantity
     */
    private String speedUnit = "m/s";
    private String mileageUnit = "m";
    private String quantityUnit = "%";


    @Autowired
    private RobotInfoService robotInfoService;

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private QuartzTriggersService quartzTriggersService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Override
    public void run(String params){
        log.info("[机器人主机]开始推送运行数据");
        //任务开始执行
        JSONObject paramObj = JSONUtil.parseObj(params);
        Long jobId = Long.valueOf((Integer) paramObj.get("jobId"));
        this.saveOperationLog(null, jobId, String.format(SysLogConstant.ROBOT_PATROL_TASK_START, "运行数据", jobId), null);


        String currentDate = CommonOutResolver.sdf.format(System.currentTimeMillis());
        List<RobotInfoEntity> robotList = robotInfoService.list();
        //封装xmlDTO
        XmlOutRobotPushCommonDTO robotPushCommonDTO = new XmlOutRobotPushCommonDTO();
        robotPushCommonDTO.setSendCode(NettyConstants.ROBOT_HOST_CODE);
        robotPushCommonDTO.setReceiveCode(NettyConstants.PATROL_HOST_CODE);
        robotPushCommonDTO.setType(NettyConstants.IN_CODE_RUN + "");
        List<XmlOutRobotPushCommonDTO.Item> items = Lists.newArrayList();
        robotList.forEach(robot -> {
//            String robotName = robot.getName();
//            String robotCode = robot.getCode();
            //运行速度
            XmlOutRobotPushCommonDTO.Item speed = new XmlOutRobotPushCommonDTO.Item();
            speed.setRobotCode(robot.getCode());
            speed.setRobotName(robot.getName());
            speed.setTime(currentDate);
            speed.setType(NettyConstants.IN_CODE_RUN_SPEED + "");
            speed.setValue(robot.getSpeed());
            speed.setValueUnit(robot.getSpeed() + speedUnit);
            speed.setUnit(speedUnit);
            //行驶里程
            XmlOutRobotPushCommonDTO.Item mileage = new XmlOutRobotPushCommonDTO.Item();
            mileage.setRobotCode(robot.getCode());
            mileage.setRobotName(robot.getName());
            mileage.setTime(currentDate);
            mileage.setType(NettyConstants.IN_CODE_RUN_MILEAGE + "");
            mileage.setValue(robot.getMileage());
            mileage.setValueUnit(robot.getMileage() + mileageUnit);
            mileage.setUnit(mileageUnit);
            //电池电量
            XmlOutRobotPushCommonDTO.Item quantity = new XmlOutRobotPushCommonDTO.Item();
            quantity.setRobotCode(robot.getCode());
            quantity.setRobotName(robot.getName());
            quantity.setTime(currentDate);
            quantity.setType(NettyConstants.IN_CODE_RUN_QUANTITY + "");
            quantity.setValue(robot.getQuantity());
            quantity.setValueUnit(robot.getQuantity() + quantityUnit);
            quantity.setUnit(quantityUnit);

            //add
            items.add(speed);
            items.add(mileage);
            items.add(quantity);

        });
        robotPushCommonDTO.setItems(items);
        String jsonStr = XmlBeanUtils.beanToXml(robotPushCommonDTO,XmlOutRobotPushCommonDTO.class);
        MessageAboutRobotDTO runDataVO = new MessageAboutRobotDTO();
        runDataVO.setMsgType(EnumSendToRobotMsgType.RUN_DATA);
        runDataVO.setMsgBody(jsonStr);
        String runDataMsg = JSONUtil.toJsonStr(runDataVO);
        MessageUtil.sendMessage(runDataMsg);
        this.saveOperationLog(null, jobId, String.format(SysLogConstant.ROBOT_PATROL_TASK_END, "运行数据", jobId), null);
        log.info("[机器人主机]推送运行数据结束");
    }


    /**
     * 添加任务执行日志
     * @param robotId
     * @param jobId
     * @param logContent
     */
    @Override
    public void saveOperationLog(Long robotId, Long jobId, String logContent, String deviceId) {
        operationLogService.saveSysLogThenSendWebSocket(SysLogConstant.ROBOT_PATROL_TASK,//日志类型:任务
                SysLogConstant.SYS_LOCAL_STATUS,//日志状态:输出
                logContent,//日志内容
                robotId,//机器人id
                jobId,//任务id
                deviceId,//设备点位id
                this.getClass().getCanonicalName());//当前类名
        //推送任务状态结果给前台
//        messagingTemplate.convertAndSend(EnumTopicDistination.PATROL_TASK.getText(), jobId);
    }

    /**
     * 生成Item
     * @param robotCode
     * @param robotName
     * @param currentDate
     * @param type
     * @param value
     * @param unit
     * @return
     */
    private XmlOutRobotPushCommonDTO.Item createItem(String robotCode, String robotName, String currentDate, String type, String value, String unit){
        XmlOutRobotPushCommonDTO.Item item = new XmlOutRobotPushCommonDTO.Item();
        item.setRobotCode(robotCode);
        item.setRobotName(robotName);
        item.setTime(currentDate);
        item.setType(type);
        item.setValue(value);
        item.setValueUnit(value + unit);
        item.setUnit(unit);
        return item;
    }
}
