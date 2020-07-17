package com.robot.host.netty.resolver.out;

import com.robot.host.common.constants.NettyConstants;
import com.robot.host.common.constants.ProtocolMessage;
import com.robot.host.common.dto.MessageAboutRobotDTO;
import com.robot.host.common.util.SessionSocketHolder;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;

/**
 * @program: com.face.ele.smart.modules.netty.xml
 * @description: 处理请求的心跳
 * @author: dadi
 * @create: 2020-05-04 11:09
 */
@Slf4j
public abstract class CommonOutResolver implements OutResolver {

    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    protected CommonOutResolver() {
    }

    public abstract boolean support(MessageAboutRobotDTO outDTO);

    @Override
    public void resolve(MessageAboutRobotDTO busiMessage) {
        //todo 机器人主机是固定值
        NioSocketChannel socketChannel = SessionSocketHolder.get(NettyConstants.ROBOT_HOST_CODE);
        if (null == socketChannel) {
            log.error("【巡视主机业务】客户端已离线{}", busiMessage);
            return;
        }
        ProtocolMessage protocol = concreteResolve(busiMessage);
        ChannelFuture future = socketChannel.writeAndFlush(protocol);
        future.addListener((ChannelFutureListener) channelFuture ->
                log.info("【巡视主机业务】主动发送消息{}，处理完成", busiMessage));
    }

    protected abstract ProtocolMessage concreteResolve(MessageAboutRobotDTO busiMessage);

}
