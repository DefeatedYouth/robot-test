package com.robot.host.netty.client;

import com.robot.host.base.service.*;
import com.robot.host.common.constants.EnumSysConfigType;
import com.robot.host.common.util.SysConfigUtil;
import com.robot.host.netty.resolver.in.*;
import com.robot.host.netty.protocol.MessageDecoder;
import com.robot.host.netty.protocol.MessageEncoder;
import com.robot.host.netty.service.RobotFileService;
import com.robot.host.quartz.service.ScheduleJobService;
import com.robot.host.common.util.RedisTemplateHelper;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.quartz.Scheduler;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.nio.ByteOrder;
import java.util.concurrent.TimeUnit;

/**
 * @author pjmike
 * @create 2018-10-24 16:35
 */
public class ClientHandlerInitilizer extends ChannelInitializer<Channel> {

    private ScheduleJobService scheduleJobService;

    private RobotInfoService robotInfoService;

    private RobotFileService robotFileService;

    private PatrolTaskService patrolTaskService;

    private NettyClient nettyClient;

    private RedisTemplateHelper redisTemplateHelper;

    private SysConfigService sysConfigService;

    private OperationLogService operationLogService;

    private SimpMessagingTemplate messagingTemplate;

    private DeviceInfoService deviceInfoService;

    private Scheduler scheduler;


    public ClientHandlerInitilizer(ScheduleJobService scheduleJobService,
                                   RobotInfoService robotInfoService,
                                   RobotFileService robotFileService,
                                   PatrolTaskService patrolTaskService,
                                   NettyClient nettyClient,
                                   RedisTemplateHelper redisTemplateHelper,
                                   SysConfigService sysConfigService,
                                   OperationLogService operationLogService,
                                   SimpMessagingTemplate messagingTemplate,
                                   DeviceInfoService deviceInfoService,
                                   Scheduler scheduler) {
        this.scheduleJobService = scheduleJobService;
        this.robotInfoService = robotInfoService;
        this.robotFileService = robotFileService;
        this.patrolTaskService = patrolTaskService;
        this.nettyClient = nettyClient;
        this.redisTemplateHelper = redisTemplateHelper;
        this.sysConfigService = sysConfigService;
        this.operationLogService = operationLogService;
        this.messagingTemplate = messagingTemplate;
        this.deviceInfoService = deviceInfoService;
        this.scheduler = scheduler;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        // 添加用于解决粘包和拆包问题的处理器
        pipeline.addLast(new LengthFieldBasedFrameDecoder(ByteOrder.LITTLE_ENDIAN, Integer.MAX_VALUE, 10, 4, 2, 0, true));
        // 添加用于进行心跳检测的处理器
//        pipeline.addLast(new IdleStateHandler(0, 0, 0));
//        pipeline.addLast(new HeartbeatHandler(nettyClient, operationLogService));
        // 添加用于根据自定义协议将消息与字节流进行相互转换的处理器
        pipeline.addLast(new MessageEncoder());
        pipeline.addLast(new MessageDecoder());
        // 添加客户端消息处理器
//        pipeline.addLast(new IdleStateHandler(Integer.valueOf(SysConfigUtil.get(EnumSysConfigType.RegisterTimeOut.getName())), 0, 0));
        MessageResolverInFactory resolverFactory = MessageResolverInFactory.getInstance();
        pipeline.addLast(new ClientMessageHandler(resolverFactory, operationLogService));
        initRosolverFactory(resolverFactory);

    }

    public void initRosolverFactory(MessageResolverInFactory resolverFactory) {
        resolverFactory.registerResolver(new RobotOperationMessageInResolver(robotInfoService, operationLogService, messagingTemplate));
        resolverFactory.registerResolver(new ModelSyncMessageInResolver(robotFileService));
        resolverFactory.registerResolver(new NoItemsResponseMessageInResolver());
        resolverFactory.registerResolver(new PatrolTaskIssueMessageInResolver(patrolTaskService, scheduleJobService,operationLogService));
        resolverFactory.registerResolver(new TaskControlMessageInResolver(scheduleJobService, patrolTaskService, operationLogService));
        resolverFactory.registerResolver(new ItemResponseMessageInResolver(sysConfigService, scheduleJobService, operationLogService, scheduler));
        resolverFactory.registerResolver(new TaskLinkageMessageInResolver(robotInfoService,operationLogService, deviceInfoService));

    }
}
