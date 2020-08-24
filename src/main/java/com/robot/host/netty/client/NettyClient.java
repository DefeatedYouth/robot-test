package com.robot.host.netty.client;

import com.robot.host.base.service.*;
import com.robot.host.netty.service.RobotFileService;
import com.robot.host.quartz.service.ScheduleJobService;
import com.robot.host.common.util.RedisTemplateHelper;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author pjmike
 * @create 2018-10-24 16:31
 */
@Slf4j
@Component
public class NettyClient  {
    private static EventLoopGroup group = new NioEventLoopGroup();
    @Value("${netty.port}")
    private int port;
    @Value("${netty.host}")
    private String host;

    private static SocketChannel socketChannel;

    @Autowired
    private ScheduleJobService scheduleJobService;

    @Autowired
    private RobotInfoService robotInfoService;

    @Autowired
    private RedisTemplateHelper redisTemplateHelper;

    @Autowired
    private PatrolTaskService patrolTaskService;

    @Autowired
    private RobotFileService robotFileService;

    @Autowired
    private SysConfigService sysConfigService;

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private DeviceInfoService deviceInfoService;

    @Autowired
    private Scheduler scheduler;

    /**
     * 启动netty服务
     */
    public void start()  {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .remoteAddress(host, port)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ClientHandlerInitilizer(scheduleJobService,
                        robotInfoService,
                        robotFileService,
                        patrolTaskService,
                        this,
                        redisTemplateHelper,
                        sysConfigService,
                        operationLogService,
                        messagingTemplate,
                        deviceInfoService,
                        scheduler));
        ChannelFuture future = bootstrap.connect();
        //客户端断线重连逻辑
        future.addListener((ChannelFutureListener) future1 -> {
            if (future1.isSuccess()) {
                log.info("连接Netty服务端成功：" + host + ":" + port);
            } else {
                log.info("连接失败，进行断线重连：" + host + ":" + port);
                future1.channel().eventLoop().schedule(() -> start(), 15, TimeUnit.SECONDS);
            }
        });
        socketChannel = (SocketChannel) future.channel();
    }
}
