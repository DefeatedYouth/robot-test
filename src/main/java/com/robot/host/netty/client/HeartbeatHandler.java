package com.robot.host.netty.client;

import com.robot.host.common.constants.NettyConstants;
import com.robot.host.common.constants.ProtocolMessage;
import com.robot.host.common.dto.RegisterInXmlDTO;
import com.robot.host.common.dto.XmlOutRegisterDTO;
import com.robot.host.netty.protocol.MessageBase;
import com.robot.host.common.util.RedisTemplateHelper;
import com.robot.host.common.util.XmlBeanUtils;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author pjmike
 * @create 2018-10-25 17:15
 */
@Slf4j
public class HeartbeatHandler extends ChannelInboundHandlerAdapter {
    private final NettyClient nettyClient;

    public HeartbeatHandler(NettyClient nettyClient, RedisTemplateHelper redisTemplateHelper) {
        this.nettyClient = nettyClient;
        this.redisTemplateHelper = redisTemplateHelper;
    }

    private Channel channel;

    private RedisTemplateHelper redisTemplateHelper;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.channel = ctx.channel();

        ping(ctx.channel());
    }

    private void ping(Channel channel) {
        String secondStr = redisTemplateHelper.get(NettyConstants.REDIS_HEART_BEAT_SECOND_KEY, "");
        int second = 10;
        if (secondStr != null) {
            second = Integer.valueOf(secondStr);
        }
        log.info("【主站与巡视主机通讯】next heart beat will send after " + second + "s.");
        ScheduledFuture<?> future = channel.eventLoop().schedule(new Runnable() {
            @Override
            public void run() {
                if (channel.isActive()) {
                    log.info("【巡视主机与机器人主机通讯】sending heart beat to the server...");
                    channel.writeAndFlush(sendHeartMsg());
                } else {
                    log.info("【巡视主机与机器人主机通讯】The connection had broken, cancel the task that will send a heart beat.");
                    channel.closeFuture();
                    throw new RuntimeException();
                }
            }
        }, second, TimeUnit.SECONDS);

        future.addListener(new GenericFutureListener() {
            @Override
            public void operationComplete(Future future) throws Exception {
                if (future.isSuccess()) {
                    ping(channel);
                }
            }
        });
    }

    private ProtocolMessage sendHeartMsg() {
        XmlOutRegisterDTO registerXmlDTO = new XmlOutRegisterDTO();

        // 返回心跳信息
        registerXmlDTO.setSendCode(NettyConstants.ROBOT_HOST_CODE);
        registerXmlDTO.setReceiveCode(NettyConstants.PATROL_HOST_CODE);
        registerXmlDTO.setType(NettyConstants.IN_OUT_CODE_SYSTEM_TYPE + "");
        registerXmlDTO.setCommand(NettyConstants.IN_CODE_SYSTEM_TYPE_COMMAND_HB + "");
        ProtocolMessage protocolMessage = new ProtocolMessage();
        String responseBodyStr = XmlBeanUtils.beanToXml(registerXmlDTO, XmlOutRegisterDTO.class);
        protocolMessage.setBody(responseBodyStr);
//        long sessionId = robotLogService.saveMasterLog("巡视主机发送心跳", null, "机器人操控请求", EnumRobotLogType.patrol_robot_request);
        protocolMessage.setSessionId(0);
        return protocolMessage;

    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (idleStateEvent.state() == IdleState.WRITER_IDLE) {
                log.info("已经10s没有发送消息给服务端");
                //向服务端送心跳包

                MessageBase.Message heartbeat = new MessageBase.Message().toBuilder().setCmd(MessageBase.Message.CommandType.HEARTBEAT_REQUEST)
                        .setRequestId(UUID.randomUUID().toString())
                        .setContent("heartbeat").build();
                //发送心跳消息，并在发送失败时关闭该连接
                ctx.writeAndFlush(heartbeat).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //如果运行过程中服务端挂了,执行重连机制
        EventLoop eventLoop = ctx.channel().eventLoop();
        eventLoop.schedule(() -> nettyClient.start(), 10L, TimeUnit.SECONDS);
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("捕获的异常：{}", cause.getMessage());
        ctx.channel().close();
    }
}
