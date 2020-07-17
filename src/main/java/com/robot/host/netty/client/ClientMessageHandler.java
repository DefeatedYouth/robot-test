package com.robot.host.netty.client;

import com.robot.host.common.constants.MessageTypeEnum;
import com.robot.host.common.constants.NettyConstants;
import com.robot.host.common.constants.ProtocolMessage;
import com.robot.host.netty.resolver.in.InResolver;
import com.robot.host.netty.resolver.in.MessageResolverInFactory;
import com.robot.host.base.service.PatrolTaskExecService;
import com.robot.host.base.service.RobotInfoService;
import com.robot.host.common.util.SessionSocketHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @program: com.face.ele.smart.modules.netty.client
 * @description:
 * @author: dadi
 * @create: 2020-05-04 11:07
 */
@Slf4j
public class ClientMessageHandler extends SimpleChannelInboundHandler<ProtocolMessage> {

    // 创建一个线程，模拟用户发送消息
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    MessageResolverInFactory resolverFactory;

    public ClientMessageHandler(MessageResolverInFactory resolverFactory) {
        this.resolverFactory = resolverFactory;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:sss");
        System.err.println("============start: 正在注冊" + sdf.format(date));
        //启动后向主站发注册请求
        super.channelActive(ctx);
        NioSocketChannel channel = (NioSocketChannel) ctx.channel();
        SessionSocketHolder.put(NettyConstants.ROBOT_HOST_CODE,channel);
        channel.writeAndFlush(ClientMessageHandlerOld.getRegisterMessage());
    }
    public void handleMasterStation(ChannelHandlerContext ctx) {
//        //通过client上报到主站
//        ProtocolMessage protocol = new ProtocolMessage();
//        NioSocketChannel socketChannel = SessionSocketHolder.getClient();
//        ;
//        if (null == socketChannel) {
//            log.error("【主站业务】客户端已离线");
//            return;
//        }
//        XmlOutMasterRegisterDTO masterStationRegisterInXmlDTO=new XmlOutMasterRegisterDTO();
//        masterStationRegisterInXmlDTO.setSendCode(NettyConstants.PATROL_HOST_CODE);
//        masterStationRegisterInXmlDTO.setReceiveCode(NettyConstants.MASTER_HOST_CODE+"");
//        masterStationRegisterInXmlDTO.setCommand(NettyConstants.IN_CODE_SYSTEM_TYPE_COMMAND_REG+"");
//        masterStationRegisterInXmlDTO.setType(NettyConstants.IN_OUT_CODE_SYSTEM_TYPE+"");
//        String bodyStr = XmlBeanUtils.beanToXml(masterStationRegisterInXmlDTO, XmlOutMasterRegisterDTO.class);
////        long sessionId = robotLogService.saveMasterLog(bodyStr, null, "机器人操控请求", EnumRobotLogType.patrol_master_request);
////        protocol.setSessionId(sessionId);
//        protocol.setBody(bodyStr);
//        ctx.writeAndFlush(protocol);
    }
    /**
     * 这里userEventTriggered()主要是在一些用户事件触发时被调用，这里我们定义的事件是进行心跳检测的
     * ping和pong消息，当前触发器会在指定的触发器指定的时间返回内如果客户端没有被读取消息或者没有写入
     * 消息到管道，则会触发当前方法
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                // 一定时间内，当前服务没有发生读取事件，也即没有消息发送到当前服务来时，
                // 其会发送一个Ping消息到服务器，以等待其响应Pong消息
                ProtocolMessage message = new ProtocolMessage();
                message.setMessageType(MessageTypeEnum.PING);
                ctx.writeAndFlush(message);
            } else if (event.state() == IdleState.WRITER_IDLE) {
                // 如果当前服务在指定时间内没有写入消息到管道，则关闭当前管道
                ctx.close();
            }
        }
    }

//    public ClientMessageHandler(RobotLogService robotLogService) {
//        this.robotLogService = robotLogService;
//    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ProtocolMessage message) throws Exception {
        log.info("客户端读取了消息{}", message);
//        Document document = XmlUtil.parseXml(msg.getBody());
//        Object type = XmlUtil.getByXPath("//Robot/Type", document, XPathConstants.STRING);
//        if(RobotType.operations.contains(type)){
//            log.info("这是一条操作指令");
//            executor.execute(new RobotOperationJob(msg,robotInfoService));
//        }else if(RobotType.taskIssues.contains(type)){
//            log.info("这是一条任务下发指令");
//            executor.execute(new CreateTaskJob(msg,patrolTaskExecService,scheduleJobService));
//        }else if(RobotType.taskControls.contains(type)){
//            log.info("这是一条任务控制指令");
//            executor.execute(new TaskControlJob(msg,patrolTaskExecService,scheduleJobService));
//        }else if(RobotType.modelSyncs.contains(type)){
//            log.info("这是一条模型同步指令");
//            ModelSyncJob.modelSync(msg);
//        }

        String body = message.getBody();
        //TODO 需要改为性能高的xml 解析工具
        if (StringUtils.isBlank(body)) {
            return;
        }
        //消息处理工厂
        InResolver resolver = resolverFactory.getMessageResolver(message);
        ProtocolMessage result = resolver.resolve(message, ctx);
        if (result != null) {
            ctx.writeAndFlush(result);
        }


    }

}
