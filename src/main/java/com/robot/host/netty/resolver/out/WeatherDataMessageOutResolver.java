package com.robot.host.netty.resolver.out;

import com.robot.host.common.constants.EnumSendToRobotMsgType;
import com.robot.host.common.constants.ProtocolMessage;
import com.robot.host.common.dto.MessageAboutRobotDTO;

public class WeatherDataMessageOutResolver extends CommonOutResolver {
    @Override
    public boolean support(MessageAboutRobotDTO outDTO) {
        EnumSendToRobotMsgType robotMsgType = outDTO.getMsgType();
        if(robotMsgType == EnumSendToRobotMsgType.WEATHER_DATA){
            return true;
        }
        return false;
    }

    @Override
    protected ProtocolMessage concreteResolve(MessageAboutRobotDTO busiMessage) {
        ProtocolMessage protocol = new ProtocolMessage();
        protocol.setBody(busiMessage.getMsgBody());
        return protocol;
    }
}
