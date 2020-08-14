package com.robot.host.netty.resolver.out;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.robot.host.base.entry.RobotInfoEntity;
import com.robot.host.base.service.OperationLogService;
import com.robot.host.base.service.RobotInfoService;
import com.robot.host.common.constants.EnumSendToRobotMsgType;
import com.robot.host.common.constants.NettyConstants;
import com.robot.host.common.constants.ProtocolMessage;
import com.robot.host.common.dto.MessageAboutRobotDTO;
import com.robot.host.common.dto.StatusDataDTO;
import com.robot.host.common.dto.XmlOutRobotPushCommonDTO;
import com.robot.host.common.util.XmlBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.collections.Lists;

import java.util.Date;
import java.util.List;

public class StatusDataMessageOutResolver extends CommonOutResolver {


    private RobotInfoService robotInfoService;
    private OperationLogService operationLogService;

    public StatusDataMessageOutResolver(RobotInfoService robotInfoService, OperationLogService operationLogService) {
        super(operationLogService);
        this.robotInfoService = robotInfoService;
        this.operationLogService = operationLogService;
    }

    @Override
    public boolean support(MessageAboutRobotDTO outDTO) {
        EnumSendToRobotMsgType robotMsgType = outDTO.getMsgType();
        if(robotMsgType == EnumSendToRobotMsgType.STATUS_DATA){
            return true;
        }
        return false;
    }

    @Override
    public String operationName() {
        return "状态数据";
    }

    @Override
    public String className() {
        return this.getClass().getCanonicalName();
    }

    @Override
    protected ProtocolMessage concreteResolve(MessageAboutRobotDTO busiMessage) {
        ProtocolMessage protocol = new ProtocolMessage();

        StatusDataDTO statusDataDTO = JSONUtil.toBean(busiMessage.getMsgBody(), StatusDataDTO.class);

        RobotInfoEntity robotInfo = robotInfoService.getOne(new QueryWrapper<RobotInfoEntity>().lambda().eq(RobotInfoEntity::getCode, statusDataDTO.getRobotCode()));

        //封装xmlDTO
        XmlOutRobotPushCommonDTO statusDataVO = new XmlOutRobotPushCommonDTO();
        statusDataVO.setSendCode(NettyConstants.ROBOT_HOST_CODE);
        statusDataVO.setReceiveCode(NettyConstants.PATROL_HOST_CODE);
        statusDataVO.setType(NettyConstants.IN_CODE_STATUS + "");
        XmlOutRobotPushCommonDTO.Item item = new XmlOutRobotPushCommonDTO.Item();
        item.setRobotCode(robotInfo.getCode());
        item.setRobotName(robotInfo.getName());
        item.setTime(new Date().toString());
        item.setType(statusDataDTO.getStatusDataType().getType() + "");
        item.setValue(statusDataDTO.getStatusDataType().getValue() + "");
        //TODO  状态数据  单位  + 值带单位
        item.setValueUnit("");
        item.setUnit("");
        List<XmlOutRobotPushCommonDTO.Item> items = Lists.newArrayList(item);
        statusDataVO.setItems(items);

        String statusDataMsg = XmlBeanUtils.beanToXml(statusDataVO, XmlOutRobotPushCommonDTO.class);
        protocol.setBody(statusDataMsg);
        return protocol;
    }
}
