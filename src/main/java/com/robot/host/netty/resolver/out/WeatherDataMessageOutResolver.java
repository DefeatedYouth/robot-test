package com.robot.host.netty.resolver.out;

import com.robot.host.base.service.OperationLogService;
import com.robot.host.common.constants.EnumSendToRobotMsgType;
import com.robot.host.common.constants.ProtocolMessage;
import com.robot.host.common.dto.MessageAboutRobotDTO;

public class WeatherDataMessageOutResolver extends CommonOutResolver {

    private OperationLogService operationLogService;

    public WeatherDataMessageOutResolver(OperationLogService operationLogService) {
        super(operationLogService);
        this.operationLogService = operationLogService;
    }

    @Override
    public boolean support(MessageAboutRobotDTO outDTO) {
        EnumSendToRobotMsgType robotMsgType = outDTO.getMsgType();
        if(robotMsgType == EnumSendToRobotMsgType.WEATHER_DATA){
            return true;
        }
        return false;
    }

    @Override
    public String operationName() {
        return "微气象数据";
    }

    @Override
    public String className() {
        return this.getClass().getCanonicalName();
    }

    @Override
    protected ProtocolMessage concreteResolve(MessageAboutRobotDTO busiMessage) {
        ProtocolMessage protocol = new ProtocolMessage();
        protocol.setBody(busiMessage.getMsgBody());
        return protocol;
    }
}
