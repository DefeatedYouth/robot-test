package com.robot.host.netty.job;

import com.alibaba.fastjson.JSONObject;
import com.robot.host.common.constants.EnumRobotOperationType;
import com.robot.host.common.constants.ProtocolMessage;
import com.robot.host.common.dto.XmlOutSendInstructionsDTO;
import com.robot.host.base.service.RobotInfoService;
import com.robot.host.common.util.XmlBeanUtils;
import com.robot.host.base.ws.WebSocket;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RobotOperationJob implements Runnable {

    private ProtocolMessage protocolMessage;

    private RobotInfoService robotInfoService;

    public RobotOperationJob(ProtocolMessage protocolMessage, RobotInfoService robotInfoService) {
        this.protocolMessage = protocolMessage;
        this.robotInfoService = robotInfoService;
    }

    @Override
    public void run() {
        //解析xml
        XmlOutSendInstructionsDTO operationDTO = XmlBeanUtils.xmlToBean(protocolMessage.getBody(), XmlOutSendInstructionsDTO.class);
        EnumRobotOperationType operationType = EnumRobotOperationType.getEnum(operationDTO.getType() + "-" + operationDTO.getCommand());
        //执行操作指令
        String robotCode = operationDTO.getCode();
        String operation = operationType.getText();
        log.info("{}正在执行{}指令",robotCode,operation);


        robotInfoService.updateCoordinate(robotCode,operationType);

        //web显示
        WebSocket webSocket = new WebSocket();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message",operationType.toString());
        jsonObject.put("To","robot");
        webSocket.onMessage(jsonObject.toString());

    }
}
