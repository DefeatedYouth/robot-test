package com.robot.host.quartz.task;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.robot.host.base.service.OperationLogService;
import com.robot.host.common.constants.EnumSendToRobotMsgType;
import com.robot.host.common.constants.EnumTopicDistination;
import com.robot.host.common.constants.NettyConstants;
import com.robot.host.common.constants.SysLogConstant;
import com.robot.host.common.dto.MessageAboutRobotDTO;
import com.robot.host.common.dto.XmlOutRobotPushCommonDTO;
import com.robot.host.base.entry.RobotInfoEntity;
import com.robot.host.base.entry.WeatherInfoEntry;
import com.robot.host.netty.resolver.out.CommonOutResolver;
import com.robot.host.base.service.RobotInfoService;
import com.robot.host.base.service.WeatherInfoService;
import com.robot.host.common.util.MessageUtil;
import com.robot.host.common.util.XmlBeanUtils;
import com.robot.host.quartz.service.QuartzTriggersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.testng.collections.Lists;

import java.util.List;

@Component("weatherDataTask")
@Slf4j
public class WeatherDataTask implements ITask{

    /**
     * 单位
     * temp
     * humidity
     * windSpeed
     */
    private String tempUnit = "℃";
    private String humidityUnit = "%";
    private String windSpeedUnit = "m/s";

    @Autowired
    private WeatherInfoService weatherInfoService;

    @Autowired
    private RobotInfoService robotInfoService;

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private QuartzTriggersService quartzTriggersService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Override
    public void run(String params) {
        log.info("[机器人主机]开始推送微气象数据");

        //任务开始执行
        JSONObject paramObj = JSONUtil.parseObj(params);
        Long jobId = Long.valueOf((Integer) paramObj.get("jobId"));
        this.saveOperationLog(null, jobId, String.format(SysLogConstant.ROBOT_PATROL_TASK_START, "微气象数据", jobId), null);

        String currentDate = CommonOutResolver.sdf.format(System.currentTimeMillis());

        List<WeatherInfoEntry> weatherInfoList = weatherInfoService.list();

        //封装xmlDTO
        XmlOutRobotPushCommonDTO robotPushCommonDTO = new XmlOutRobotPushCommonDTO();
        robotPushCommonDTO.setSendCode(NettyConstants.ROBOT_HOST_CODE);
        robotPushCommonDTO.setReceiveCode(NettyConstants.PATROL_HOST_CODE);
        robotPushCommonDTO.setType(NettyConstants.IN_CODE_WEATHER + "");
        List<XmlOutRobotPushCommonDTO.Item> items = Lists.newArrayList();
        weatherInfoList.forEach(weather -> {
            RobotInfoEntity robotInfo = robotInfoService.getOne(new QueryWrapper<RobotInfoEntity>().lambda().eq(RobotInfoEntity::getCode, weather.getRobotCode()));
            //温度
            XmlOutRobotPushCommonDTO.Item temp = new XmlOutRobotPushCommonDTO.Item();
            temp.setRobotName(robotInfo.getName());
            temp.setRobotCode(robotInfo.getCode());
            temp.setType(NettyConstants.IN_CODE_WEATHER_TEMP + "");
            temp.setTime(currentDate);
            temp.setValue(weather.getTemp());
            temp.setUnit(tempUnit);
            temp.setValueUnit(weather.getTemp() + tempUnit);

            //湿度
            XmlOutRobotPushCommonDTO.Item humidity = new XmlOutRobotPushCommonDTO.Item();
            humidity.setRobotName(robotInfo.getName());
            humidity.setRobotCode(robotInfo.getCode());
            humidity.setType(NettyConstants.IN_CODE_WEATHER_HUMIDITY + "");
            humidity.setTime(currentDate);
            humidity.setValue(weather.getHumidity());
            humidity.setUnit(humidityUnit);
            humidity.setValueUnit(weather.getHumidity() + humidityUnit);

            //风速
            XmlOutRobotPushCommonDTO.Item windSpeed = new XmlOutRobotPushCommonDTO.Item();
            windSpeed.setRobotName(robotInfo.getName());
            windSpeed.setRobotCode(robotInfo.getCode());
            windSpeed.setType(NettyConstants.IN_CODE_WEATHER_WIND_SPEED + "");
            windSpeed.setTime(currentDate);
            windSpeed.setValue(weather.getWindSpeed());
            windSpeed.setUnit(windSpeedUnit);
            windSpeed.setValueUnit(weather.getWindSpeed() + windSpeedUnit);

            //add
            items.add(temp);
            items.add(humidity);
            items.add(windSpeed);
        });
        robotPushCommonDTO.setItems(items);
        String jsonStr = XmlBeanUtils.beanToXml(robotPushCommonDTO,XmlOutRobotPushCommonDTO.class);
        MessageAboutRobotDTO weatherDataVO = new MessageAboutRobotDTO();
        weatherDataVO.setMsgType(EnumSendToRobotMsgType.WEATHER_DATA);
        weatherDataVO.setMsgBody(jsonStr);
        String weatherDataMsg = JSONUtil.toJsonStr(weatherDataVO);
        MessageUtil.sendMessage(weatherDataMsg);

        this.saveOperationLog(null, jobId, String.format(SysLogConstant.ROBOT_PATROL_TASK_END, "微气象数据", jobId), null);

        log.info("[机器人主机]推送微气象数据结束");
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
                SysLogConstant.SYS_LOCAL_STATUS,//日志状态:本地
                logContent,//日志内容
                robotId,//机器人id
                jobId,//任务id
                deviceId,//设备点位id
                this.getClass().getCanonicalName());//当前类名
        //推送任务状态结果给前台
//        messagingTemplate.convertAndSend(EnumTopicDistination.PATROL_TASK.getText(), jobId);
    }
}
