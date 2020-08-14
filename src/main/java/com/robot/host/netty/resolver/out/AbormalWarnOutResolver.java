package com.robot.host.netty.resolver.out;

import com.robot.host.base.service.OperationLogService;
import com.robot.host.common.constants.EnumSendToRobotMsgType;
import com.robot.host.common.constants.ProtocolMessage;
import com.robot.host.common.dto.MessageAboutRobotDTO;

public class AbormalWarnOutResolver extends CommonOutResolver {

    private OperationLogService operationLogService;

    public AbormalWarnOutResolver(OperationLogService operationLogService) {
        super(operationLogService);
        this.operationLogService = operationLogService;
    }

    @Override
    public boolean support(MessageAboutRobotDTO outDTO) {
        EnumSendToRobotMsgType robotMsgType = outDTO.getMsgType();
        if(robotMsgType == EnumSendToRobotMsgType.ABNORMAL_WARN){
            return true;
        }
        return false;
    }

    @Override
    public String operationName() {
        return "异常告警";
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
