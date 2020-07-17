package com.robot.host.netty.resolver.in;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.robot.host.base.entry.RobotInfoEntity;
import com.robot.host.common.constants.EnumRobotComplusStatusDataType;
import com.robot.host.common.constants.EnumRobotOperationType;
import com.robot.host.common.constants.NettyConstants;
import com.robot.host.common.constants.ProtocolMessage;
import com.robot.host.common.dto.BaseXmlDTO;
import com.robot.host.common.dto.MessageJudgeInDTO;
import com.robot.host.common.dto.XmlOutSendInstructionsDTO;
import com.robot.host.base.service.RobotInfoService;
import com.robot.host.common.util.MessageUtil;
import com.robot.host.common.util.XmlBeanUtils;
import com.robot.host.base.ws.WebSocket;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RobotOperationMessageInResolver implements InResolver {

    private RobotInfoService robotInfoService;

    public RobotOperationMessageInResolver(RobotInfoService robotInfoService) {
        this.robotInfoService = robotInfoService;
    }

    @Override
    public boolean support(MessageJudgeInDTO judgeInDTO) {
        String code = judgeInDTO.getType() + "-" + judgeInDTO.getCommand();
        EnumRobotOperationType operationType = EnumRobotOperationType.getEnum(code);
        if(operationType == null){
            return false;
        }
        return true;
    }

    @Override
    public ProtocolMessage resolve(ProtocolMessage message, ChannelHandlerContext ctx) {
        //解析xml
        XmlOutSendInstructionsDTO operationDTO = XmlBeanUtils.xmlToBean(message.getBody(), XmlOutSendInstructionsDTO.class);
        EnumRobotOperationType operationType = EnumRobotOperationType.getEnum(operationDTO.getType() + "-" + operationDTO.getCommand());
        //执行操作指令
        String robotCode = operationDTO.getCode();
        String operation = operationType.getText();
        log.info("{}正在执行{}指令",robotCode,operation);

        if(operationType.equals(EnumRobotOperationType.shoudongchongdian)){
            RobotInfoEntity robotInfo = robotInfoService.getOne(new QueryWrapper<RobotInfoEntity>().lambda().eq(RobotInfoEntity::getCode, robotCode));
            robotInfo.setStatus(3);
            robotInfoService.saveOrUpdate(robotInfo);
            MessageUtil.statusDataMessage(robotCode, EnumRobotComplusStatusDataType.YunXingZhuangTai_ChongDian,null,null);
        }else {
            robotInfoService.updateCoordinate(robotCode, operationType);
        }


        //web显示
        WebSocket webSocket = new WebSocket();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message",operationType.toString());
        jsonObject.put("To","robot");
        webSocket.onMessage(jsonObject.toString());

        //响应
        ProtocolMessage protocol = new ProtocolMessage();
        BaseXmlDTO operationVO = new BaseXmlDTO();
        operationVO.setSendCode(NettyConstants.ROBOT_HOST_CODE);
        operationVO.setReceiveCode(NettyConstants.PATROL_HOST_CODE);
        operationVO.setType(NettyConstants.IN_OUT_CODE_SYSTEM_TYPE + "");
        operationVO.setCommand(NettyConstants.OUT_CODE_SYSTEM_TYPE_COMMAND_COMMON_NO_ITEMS + "");
        operationVO.setCode(NettyConstants.RESPONSE_CODE_SUCCESS + "");
        String operationmsg = XmlBeanUtils.beanToXml(operationVO, BaseXmlDTO.class);
        protocol.setBody(operationmsg);
        return protocol;
    }
}
