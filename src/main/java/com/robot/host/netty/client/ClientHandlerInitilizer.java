package com.robot.host.netty.client;

import com.robot.host.base.service.PatrolTaskService;
import com.robot.host.netty.resolver.in.*;
import com.robot.host.netty.protocol.MessageDecoder;
import com.robot.host.netty.protocol.MessageEncoder;
import com.robot.host.netty.service.RobotFileService;
import com.robot.host.quartz.service.ScheduleJobService;
import com.robot.host.base.service.PatrolTaskExecService;
import com.robot.host.base.service.RobotInfoService;
import com.robot.host.common.util.RedisTemplateHelper;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.nio.ByteOrder;

/**
 * @author pjmike
 * @create 2018-10-24 16:35
 */
public class ClientHandlerInitilizer extends ChannelInitializer<Channel> {

    private PatrolTaskExecService patrolTaskExecService;

    private ScheduleJobService scheduleJobService;

    private RobotInfoService robotInfoService;

    private RobotFileService robotFileService;

    private PatrolTaskService patrolTaskService;

    private NettyClient nettyClient;

    private RedisTemplateHelper redisTemplateHelper;

    public ClientHandlerInitilizer(PatrolTaskExecService patrolTaskExecService, ScheduleJobService scheduleJobService, RobotInfoService robotInfoService, NettyClient nettyClient, RedisTemplateHelper redisTemplateHelper) {
        this.patrolTaskExecService = patrolTaskExecService;
        this.scheduleJobService = scheduleJobService;
        this.robotInfoService = robotInfoService;
        this.nettyClient = nettyClient;
        this.redisTemplateHelper = redisTemplateHelper;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        // 添加用于解决粘包和拆包问题的处理器
        pipeline.addLast(new LengthFieldBasedFrameDecoder(ByteOrder.LITTLE_ENDIAN, Integer.MAX_VALUE, 10, 4, 2, 0, true));
        // 添加用于进行心跳检测的处理器
       // pipeline.addLast(new IdleStateHandler(1, 2, 0));
//        pipeline.addLast(new HeartbeatHandler(nettyClient,redisTemplateHelper));
        // 添加用于根据自定义协议将消息与字节流进行相互转换的处理器
        pipeline.addLast(new MessageEncoder());
        pipeline.addLast(new MessageDecoder());
        // 添加客户端消息处理器
        MessageResolverInFactory resolverFactory = MessageResolverInFactory.getInstance();
        initRosolverFactory(resolverFactory);
        pipeline.addLast(new ClientMessageHandler(resolverFactory));
    }

    public void initRosolverFactory(MessageResolverInFactory resolverFactory) {
        resolverFactory.registerResolver(new RobotOperationMessageInResolver(robotInfoService));
        resolverFactory.registerResolver(new ModelSyncMessageInResolver(robotFileService));
        resolverFactory.registerResolver(new NoItemsResponseMessageInResolver());
        resolverFactory.registerResolver(new PatrolTaskIssueMessageInResolver(patrolTaskService,scheduleJobService));
        resolverFactory.registerResolver(new TaskControlMessageInResolver(scheduleJobService,patrolTaskService));

    }
}
