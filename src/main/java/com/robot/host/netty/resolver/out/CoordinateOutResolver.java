package com.robot.host.netty.resolver.out;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.robot.host.base.service.OperationLogService;
import com.robot.host.common.constants.EnumSendToRobotMsgType;
import com.robot.host.common.constants.NettyConstants;
import com.robot.host.common.constants.ProtocolMessage;
import com.robot.host.common.dto.MessageAboutRobotDTO;
import com.robot.host.common.dto.RobotCoordinateDTO;
import com.robot.host.common.dto.XmlOutRobotCoordinateDTO;
import com.robot.host.base.entry.RobotInfoEntity;
import com.robot.host.base.service.RobotInfoService;
import com.robot.host.common.util.XmlBeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.testng.collections.Lists;

import java.util.Date;
import java.util.List;

@Slf4j
public class CoordinateOutResolver extends CommonOutResolver {

    private RobotInfoService robotInfoService;

    private OperationLogService operationLogService;

    public CoordinateOutResolver(RobotInfoService robotInfoService, OperationLogService operationLogService) {
        super(operationLogService);
        this.operationLogService = operationLogService;
        this.robotInfoService = robotInfoService;
    }

    @Override
    public boolean support(MessageAboutRobotDTO outDTO) {
        EnumSendToRobotMsgType robotMsgType = outDTO.getMsgType();
        if(robotMsgType == EnumSendToRobotMsgType.COORDINATE_DATA){
            return true;
        }
        return false;
    }

    @Override
    public String operationName() {
        return "坐标";
    }

    @Override
    public String className() {
        return this.getClass().getCanonicalName();
    }

    @Override
    protected ProtocolMessage concreteResolve(MessageAboutRobotDTO busiMessage) {
        ProtocolMessage protocol = new ProtocolMessage();


        //发送给巡视主机的消息体
        String bodyStr = null;

        RobotCoordinateDTO robotDTO = JSONUtil.toBean(busiMessage.getMsgBody(), RobotCoordinateDTO.class);
        RobotInfoEntity robotInfo = robotInfoService.getOne(new QueryWrapper<RobotInfoEntity>().lambda().eq(RobotInfoEntity::getCode, robotDTO.getRobotCode()));

        //封装xmlDTO
        XmlOutRobotCoordinateDTO coordinateDTO = new XmlOutRobotCoordinateDTO();
        coordinateDTO.setSendCode(NettyConstants.ROBOT_HOST_CODE);
        coordinateDTO.setReceiveCode(NettyConstants.PATROL_HOST_CODE);
        coordinateDTO.setType(NettyConstants.IN_CODE_Coordinate + "");

        XmlOutRobotCoordinateDTO.Item item = new XmlOutRobotCoordinateDTO.Item();
        item.setRobotName(robotInfo.getName());
        //TODO: 文件名称
        item.setFilePath("");
        item.setRobotCode(robotInfo.getCode());
        //当前时间
        item.setTime(sdf.format(new Date()));
        item.setCoordinatePixel(robotInfo.getPosX() + "," + robotInfo.getPosY());
        item.setCoordinateGeography(robotInfo.getLongitude() + "," + robotInfo.getLatitude());
        List<XmlOutRobotCoordinateDTO.Item> items = Lists.newArrayList(item);
        coordinateDTO.setItems(items);
        bodyStr = XmlBeanUtils.beanToXml(coordinateDTO, XmlOutRobotCoordinateDTO.class);

        protocol.setBody(bodyStr);
        //TODO 设置sessionId
        protocol.setSessionId(0);
        return protocol;
    }
}
