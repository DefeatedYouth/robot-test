package com.robot.host.netty.resolver.in;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.robot.host.base.entry.OperationLogEntity;
import com.robot.host.base.entry.RobotInfoEntity;
import com.robot.host.base.service.OperationLogService;
import com.robot.host.common.constants.*;
import com.robot.host.common.dto.BaseXmlDTO;
import com.robot.host.common.dto.MessageJudgeInDTO;
import com.robot.host.common.dto.XmlOutSendInstructionsDTO;
import com.robot.host.base.service.RobotInfoService;
import com.robot.host.common.util.MessageUtil;
import com.robot.host.common.util.XmlBeanUtils;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Slf4j
public class RobotOperationMessageInResolver implements InResolver {

    public static final String OPERATION_CONTENT = "[%s]%s正在执行%s指令";

    private RobotInfoService robotInfoService;

    private OperationLogService operationLogService;

    private SimpMessagingTemplate messagingTemplate;

    public RobotOperationMessageInResolver(RobotInfoService robotInfoService, OperationLogService operationLogService, SimpMessagingTemplate messagingTemplate) {
        this.robotInfoService = robotInfoService;
        this.operationLogService = operationLogService;
        this.messagingTemplate = messagingTemplate;
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
        RobotInfoEntity robotInfo = robotInfoService.getOne(new QueryWrapper<RobotInfoEntity>().lambda().eq(RobotInfoEntity::getCode, robotCode));

        //添加操作日志
        OperationLogEntity logEntity = operationLogService.saveSysLogThenSendWebSocket(SysLogConstant.ROBOT_ROBOT_OPERATION,
                SysLogConstant.SYS_LOCAL_STATUS,
                String.format(OPERATION_CONTENT, operationName(), robotInfo.getCode(), operationType.getText()),
                robotInfo.getRobotInfoId(), null, null, className());

        if(operationType.equals(EnumRobotOperationType.shoudongchongdian)){
            robotInfo.setStatus(3);
            robotInfoService.saveOrUpdate(robotInfo);
            MessageUtil.statusDataMessage(robotCode, EnumRobotComplusStatusDataType.YunXingZhuangTai_ChongDian);
        }else if(operationType.equals(EnumRobotOperationType.KongZhiMoShiQieHuan)) {
            //切换任务模式
            this.updateControlMode(operationDTO.getItems(), robotCode);
        }else if(operationType.getType().equals(2)){
            robotInfoService.updateCoordinate(robotCode, operationType);
        }else{

        }



        //推送数据
        JSONObject returnMessage = new JSONObject();
        returnMessage.put("robotCode",robotCode);
        returnMessage.put("operation",operationType.toString());
        returnMessage.put("operationLog",logEntity);
        messagingTemplate.convertAndSend(EnumTopicDistination.ROBOT_OPERATION.getText(),returnMessage);

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

    //修改控制模式并返回状态数据
    private void updateControlMode(List<XmlOutSendInstructionsDTO.Item> items, String robotCode) {
        XmlOutSendInstructionsDTO.Item item = items.isEmpty() ? null : items.get(0);
        if(item != null){
            String value = item.getValue();
            String fullCode = EnumRobotStatusDataType.taskModel.getType() + "-" + value;
            EnumRobotComplusStatusDataType status = EnumRobotComplusStatusDataType.getEnum(fullCode);
            MessageUtil.statusDataMessage(robotCode,status);
        }
    }

    @Override
    public String operationName() {
        return "机器人控制";
    }

    @Override
    public String className() {
        return this.getClass().getCanonicalName();
    }
}
