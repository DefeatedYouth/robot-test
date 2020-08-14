package com.robot.host.netty.resolver.out;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.robot.host.base.entry.DeviceInfoEntry;
import com.robot.host.base.service.DeviceInfoService;
import com.robot.host.base.service.OperationLogService;
import com.robot.host.common.constants.EnumSendToRobotMsgType;
import com.robot.host.common.constants.NettyConstants;
import com.robot.host.common.constants.ProtocolMessage;
import com.robot.host.common.dto.MessageAboutRobotDTO;
import com.robot.host.common.dto.PatrolRouteDTO;
import com.robot.host.common.dto.XmlOutRobotPatrolRouteDTO;
import com.robot.host.base.entry.RobotInfoEntity;
import com.robot.host.base.entry.SceneInfoEntity;
import com.robot.host.base.service.RobotInfoService;
import com.robot.host.base.service.SceneInfoService;
import com.robot.host.common.util.XmlBeanUtils;
import org.testng.collections.Lists;

import java.util.List;

public class PatrolRouteOutResolver extends CommonOutResolver {

    private SceneInfoService sceneInfoService;

    private RobotInfoService robotInfoService;

    private DeviceInfoService deviceInfoService;

    private OperationLogService operationLogService;

    public PatrolRouteOutResolver(SceneInfoService sceneInfoService, RobotInfoService robotInfoService, DeviceInfoService deviceInfoService, OperationLogService operationLogService) {
        super(operationLogService);
        this.operationLogService = operationLogService;
        this.sceneInfoService = sceneInfoService;
        this.robotInfoService = robotInfoService;
        this.deviceInfoService = deviceInfoService;
    }

    @Override
    public boolean support(MessageAboutRobotDTO outDTO) {
        EnumSendToRobotMsgType robotMsgType = outDTO.getMsgType();
        if(robotMsgType == EnumSendToRobotMsgType.PATROL_ROUTE){
            return true;
        }
        return false;
    }

    @Override
    public String operationName() {
        return "巡视线路";
    }

    @Override
    public String className() {
        return this.getClass().getCanonicalName();
    }

    @Override
    protected ProtocolMessage concreteResolve(MessageAboutRobotDTO busiMessage) {
        ProtocolMessage protocol = new ProtocolMessage();
        //获取scene
        PatrolRouteDTO patrolRouteDTO = JSONUtil.toBean(busiMessage.getMsgBody(), PatrolRouteDTO.class);
        RobotInfoEntity robotInfo = robotInfoService.getOne(new QueryWrapper<RobotInfoEntity>().lambda().eq(RobotInfoEntity::getCode, patrolRouteDTO.getRobotCode()));
        List<DeviceInfoEntry> deviceInfoList = deviceInfoService.list(new QueryWrapper<DeviceInfoEntry>().lambda().in(DeviceInfoEntry::getDeviceId, Lists.newArrayList(patrolRouteDTO.getSceneIds().split(","))));
        //当前时间
        String currentDate = CommonOutResolver.sdf.format(System.currentTimeMillis());

        //封装XML
        XmlOutRobotPatrolRouteDTO patrolRouteVO = new XmlOutRobotPatrolRouteDTO();
        patrolRouteVO.setSendCode(NettyConstants.ROBOT_HOST_CODE);
        patrolRouteVO.setReceiveCode(NettyConstants.PATROL_HOST_CODE);
        patrolRouteVO.setType(NettyConstants.IN_CODE_PATROL_ROUTE + "");

        List<XmlOutRobotPatrolRouteDTO.Item> items = Lists.newArrayList();
        deviceInfoList.forEach(device -> {
            XmlOutRobotPatrolRouteDTO.Item item = new XmlOutRobotPatrolRouteDTO.Item();
            item.setRobotName(robotInfo.getName());
            item.setRobotCode(robotInfo.getCode());
            item.setFilePath("");
            item.setTime(currentDate);
            item.setCoordinatePixel(device.getPosX() + "," + device.getPosY());
            item.setCoordinateGeography("");
            items.add(item);
        });
        patrolRouteVO.setItems(items);
        String patrolRouteMsg = XmlBeanUtils.beanToXml(patrolRouteVO,XmlOutRobotPatrolRouteDTO.class);
        protocol.setBody(patrolRouteMsg);
        return protocol;
    }
}
