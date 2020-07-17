package com.robot.host.common.util;

import cn.hutool.json.JSONUtil;
import com.robot.host.common.constants.EnumRobotComplusStatusDataType;
import com.robot.host.common.constants.EnumSendToRobotMsgType;
import com.robot.host.common.dto.MessageAboutRobotDTO;
import com.robot.host.common.dto.StatusDataDTO;
import com.robot.host.netty.resolver.out.MessageResolverOutFactory;
import com.robot.host.netty.resolver.out.OutResolver;

public class MessageUtil {



    public static void sendMessage(String msg){
        MessageAboutRobotDTO busiMessage = JSONUtil.toBean(msg, MessageAboutRobotDTO.class);

        MessageResolverOutFactory resolverFactory = MessageResolverOutFactory.getInstance();
        OutResolver resolver = resolverFactory.getMessageResolver(busiMessage);
        resolver.resolve(busiMessage);
    }

    /**
     * 状态数据封装
     */
    public static void statusDataMessage(String robotCode, EnumRobotComplusStatusDataType status, String value, String unit){
        StatusDataDTO statusDataDTO = new StatusDataDTO();
        statusDataDTO.setRobotCode(robotCode);
        statusDataDTO.setStatusDataType(status);
        statusDataDTO.setStatusValue(value);
        statusDataDTO.setStatusUnit(unit);
        String statusDataMsg = JSONUtil.toJsonStr(statusDataDTO);
        MessageAboutRobotDTO messageAboutRobotDTO = new MessageAboutRobotDTO();
        messageAboutRobotDTO.setMsgType(EnumSendToRobotMsgType.STATUS_DATA);
        messageAboutRobotDTO.setMsgBody(statusDataMsg);
        String jsonStr = JSONUtil.toJsonStr(messageAboutRobotDTO);
        sendMessage(jsonStr);
    }
}
