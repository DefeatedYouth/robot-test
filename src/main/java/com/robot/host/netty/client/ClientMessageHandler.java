package com.robot.host.netty.client;

import com.robot.host.base.service.OperationLogService;
import com.robot.host.common.constants.NettyConstants;
import com.robot.host.common.constants.ProtocolMessage;
import com.robot.host.common.constants.SysLogConstant;
import com.robot.host.common.dto.XmlOutRegisterDTO;
import com.robot.host.common.util.XmlBeanUtils;
import com.robot.host.netty.resolver.in.InResolver;
import com.robot.host.netty.resolver.in.MessageResolverInFactory;
import com.robot.host.common.util.SessionSocketHolder;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Slf4j
public class ClientMessageHandler extends SimpleChannelInboundHandler<ProtocolMessage> {

    // 创建一个线程，模拟用户发送消息
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    MessageResolverInFactory resolverFactory;

    private OperationLogService operationLogService;

    public ClientMessageHandler(MessageResolverInFactory resolverFactory, OperationLogService operationLogService) {
        this.resolverFactory = resolverFactory;
        this.operationLogService = operationLogService;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //启动后向主站发注册请求
        super.channelActive(ctx);
        robotRegister(ctx);
    }

    private void robotRegister(ChannelHandlerContext ctx) {
        NioSocketChannel channel = (NioSocketChannel)ctx.channel();
        //TODO: 测试专用  注册成功，添加注册信息
//        SessionSocketHolder.put(NettyConstants.ROBOT_HOST_CODE,channel);
        log.info("【机器人主机业务】:主动发送注册信息");
        //注册信息
        XmlOutRegisterDTO robotRegisterDTO = new XmlOutRegisterDTO();
        robotRegisterDTO.setSendCode(NettyConstants.ROBOT_HOST_CODE);
        robotRegisterDTO.setReceiveCode(NettyConstants.PATROL_HOST_CODE);
        robotRegisterDTO.setType(NettyConstants.IN_OUT_CODE_SYSTEM_TYPE + "");
        robotRegisterDTO.setCommand(NettyConstants.IN_CODE_SYSTEM_TYPE_COMMAND_REG + "");
        robotRegisterDTO.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        String registerMsg = XmlBeanUtils.beanToXml(robotRegisterDTO, XmlOutRegisterDTO.class);
        ProtocolMessage protocol = new ProtocolMessage();
        protocol.setBody(registerMsg);
        operationLogService.saveSysLogThenSendWebSocket(SysLogConstant.ROBOT_OTHER,
                SysLogConstant.SYS_OUTPUT_STATUS,
                String.format(SysLogConstant.IN_OUTPUT_MESSAGE, "注册", registerMsg),
                null,null,null,this.getClass().getCanonicalName());
        channel.writeAndFlush(protocol);
    }


    /**
     *注册失败，重新发起注册
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                // 一定时间内，当前服务没有发生读取事件，也即没有消息发送到当前服务来时，
                // 检查是否注册成功
                NioSocketChannel client = SessionSocketHolder.getClient();
                if(client == null){
                    log.info("【机器人主机业务】注册失败，重新注册");
                    operationLogService.saveSysLogThenSendWebSocket(SysLogConstant.ROBOT_OTHER,
                            SysLogConstant.SYS_LOCAL_STATUS,
                            "注册响应超时，重新发起注册",
                            null,null,null,this.getClass().getCanonicalName());
                    robotRegister(ctx);
                }

            } else {
                super.userEventTriggered(ctx,evt);
            }
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ProtocolMessage message){
        log.info("[机器人主机]接受消息：{}", message.getBody());
        String body = message.getBody();
        //TODO 需要改为性能高的xml 解析工具
        if (StringUtils.isBlank(body)) {
            return;
        }
        //消息处理工厂
        InResolver resolver = resolverFactory.getMessageResolver(message);
        //消息接收日志
        operationLogService.saveSysLogThenSendWebSocket(SysLogConstant.ROBOT_OTHER,
                SysLogConstant.SYS_INPUT_STATUS,
                String.format(SysLogConstant.OUT_INPUT_MESSAGE, resolver.operationName(), message.getBody()),
                null, null, null, resolver.className());
        ProtocolMessage result = resolver.resolve(message, ctx);
        if (result != null) {
            //消息响应日志
            operationLogService.saveSysLogThenSendWebSocket(SysLogConstant.ROBOT_OTHER,
                    SysLogConstant.SYS_OUTPUT_STATUS,
                    String.format(SysLogConstant.IN_OUTPUT_RESP_MESSAGE, resolver.operationName(), result.getBody()),
                    null, null, null, resolver.className());
            ctx.writeAndFlush(result);
        }


    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();

    }
}
