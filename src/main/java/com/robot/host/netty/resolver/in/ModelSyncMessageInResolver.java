package com.robot.host.netty.resolver.in;

import com.robot.host.common.dto.MessageJudgeInDTO;
import com.robot.host.common.constants.NettyConstants;
import com.robot.host.common.constants.ProtocolMessage;
import com.robot.host.common.dto.XmlInRobotMoelSynDTO;
import com.robot.host.netty.service.RobotFileService;
import com.robot.host.common.util.SessionSocketHolder;
import com.robot.host.common.util.XmlBeanUtils;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;


@Slf4j
public class ModelSyncMessageInResolver implements InResolver {

    private RobotFileService robotFileService;

    public ModelSyncMessageInResolver(RobotFileService robotFileService) {
        this.robotFileService = robotFileService;
    }

    @Override
    public boolean support(MessageJudgeInDTO judgeInDTO) {
        if (!StringUtils.equals(judgeInDTO.getType(), NettyConstants.OUT_CODE_MODEL_SYN + "")) {
            return false;
        }
        if (!StringUtils.equals(judgeInDTO.getCommand(), NettyConstants.OUT_CODE_MODEL_SYN_COMMAND + "")) {
            return false;
        }
        return true;
    }

    @Override
    public ProtocolMessage resolve(ProtocolMessage message, ChannelHandlerContext ctx) {
        //接收消息解析
        String requestBodyStr = message.getBody();
        XmlInRobotMoelSynDTO inModelSyncDTO = XmlBeanUtils.xmlToBean(requestBodyStr, XmlInRobotMoelSynDTO.class);

        /**
         * 生成文件，获取路径
         * deviceFilePath 设备模型路径
         * robotFilePath 机器人模型路径
         */
        String deviceFilePath = robotFileService.uploadDeviceFile();
        String robotFilePath = robotFileService.uploadRobotFile();


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
        returnMsg.setSessionId(message.getSessionId());
        returnMsg.setBody(jsonMsg);

        NioSocketChannel channel = SessionSocketHolder.get(NettyConstants.ROBOT_HOST_CODE);
        ChannelFuture future = channel.writeAndFlush(jsonMsg);
        future.addListener((ChannelFutureListener) channelFuture ->
                log.info("[模型同步]响应信息:{}",jsonMsg));
        return null;
    }
}
