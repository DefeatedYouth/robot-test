package com.robot.host.netty.job;

import com.robot.host.common.constants.NettyConstants;
import com.robot.host.common.constants.ProtocolMessage;
import com.robot.host.common.dto.XmlInRobotMoelSynDTO;
import com.robot.host.base.entry.DeviceInfoEntry;
import com.robot.host.base.entry.RobotInfoEntity;
import com.robot.host.base.service.DeviceInfoService;
import com.robot.host.base.service.RobotInfoService;
import com.robot.host.common.util.EasyExcelUtil;
import com.robot.host.common.util.SessionSocketHolder;
import com.robot.host.common.util.XmlBeanUtils;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class ModelSyncJob implements Runnable {

    public static String deviceFilePath = "";

    public static String robotFilePath = "";


    private ProtocolMessage protocolMessage;

    private DeviceInfoService deviceInfoService;

    private RobotInfoService robotInfoService;


    @Override
    public void run() {

        InputStream is = null;

        //导入设备
        List<DeviceInfoEntry> deviceList = EasyExcelUtil.syncReadModel(is, DeviceInfoEntry.class, 0, 1);
        deviceInfoService.saveBatch(deviceList);

        //导入机器人
        List<RobotInfoEntity> robotList = EasyExcelUtil.syncReadModel(is, RobotInfoEntity.class, 0, 1);
        robotInfoService.saveBatch(robotList);

    }


    public static void modelSync(ProtocolMessage protocolMessage){
        //接收消息解析
        XmlInRobotMoelSynDTO inModelSyncDTO = XmlBeanUtils.xmlToBean(protocolMessage.getBody(), XmlInRobotMoelSynDTO.class);
        //构建响应消息
        XmlInRobotMoelSynDTO outModelSyncDTO = new XmlInRobotMoelSynDTO();
        outModelSyncDTO.setSendCode(NettyConstants.ROBOT_HOST_CODE);
        outModelSyncDTO.setReceiveCode(NettyConstants.PATROL_HOST_CODE);

        outModelSyncDTO.setType(NettyConstants.IN_OUT_CODE_SYSTEM_TYPE + "");
        outModelSyncDTO.setCommand(NettyConstants.OUT_CODE_SYSTEM_TYPE_COMMAND_COMMON_ITEMS + "");
        outModelSyncDTO.setCode(NettyConstants.RESPONSE_CODE_SUCCESS + "");

        XmlInRobotMoelSynDTO.Item item = new XmlInRobotMoelSynDTO.Item();
        item.setDeviceFilePath(deviceFilePath);
        item.setRobotFilePath(robotFilePath);

        outModelSyncDTO.setItems(Arrays.asList(item));

        String jsonMsg = XmlBeanUtils.beanToXml(outModelSyncDTO,XmlInRobotMoelSynDTO.class);

        ProtocolMessage returnMsg = new ProtocolMessage();
        returnMsg.setSessionId(protocolMessage.getSessionId());
        returnMsg.setBody(jsonMsg);

        NioSocketChannel channel = SessionSocketHolder.get(NettyConstants.ROBOT_HOST_CODE);
        ChannelFuture future = channel.writeAndFlush(jsonMsg);
        future.addListener((ChannelFutureListener) channelFuture ->
                log.info("[模型同步]响应信息:{}",jsonMsg));

    }
}
