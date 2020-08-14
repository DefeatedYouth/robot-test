package com.robot.host.common.config;

import com.robot.host.base.service.SysConfigService;
import com.robot.host.base.service.impl.SysConfigServiceImpl;
import com.robot.host.netty.client.NettyClient;
import com.robot.host.netty.config.OutResolverConfig;
import com.robot.host.quartz.service.ScheduleJobService;
import com.robot.host.quartz.service.impl.ScheduleJobServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * 初始化配置类
 */
@Configuration
public class initConfig {

    @Autowired
    private SysConfigServiceImpl sysConfigService;

    @Autowired
    private ScheduleJobServiceImpl scheduleJobService;

    @Autowired
    private NettyClient nettyClient;

    @Autowired
    private OutResolverConfig outResolverConfig;


    @PostConstruct
    public void init(){
        //初始化配置信息
        sysConfigService.init();
        //启动netty
        nettyClient.start();
        //初始化job
        scheduleJobService.init();
        //消息发送
        outResolverConfig.init();
    }
}
