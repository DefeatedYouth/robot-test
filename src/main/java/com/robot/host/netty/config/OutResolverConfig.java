package com.robot.host.netty.config;

import com.robot.host.base.service.*;
import com.robot.host.netty.resolver.out.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class OutResolverConfig {


    MessageResolverOutFactory resolverFactory = MessageResolverOutFactory.getInstance();

    @Autowired
    private RobotInfoService robotInfoService;

    @Autowired
    private PatrolTaskService patrolTaskService;

    @Autowired
    private PatrolTaskExecService patrolTaskExecService;

    @Autowired
    private SceneInfoService sceneInfoService;

    @Autowired
    private PatrolTaskResultService patrolTaskResultService;

    @Autowired
    private DeviceInfoService deviceInfoService;

    @PostConstruct
    public void init(){
        initRosolverFactory(resolverFactory);
    }


    public void initRosolverFactory(MessageResolverOutFactory resolverFactory) {
        resolverFactory.registerResolver(new RunDataMessageOutResolver());
        resolverFactory.registerResolver(new WeatherDataMessageOutResolver());
        resolverFactory.registerResolver(new CoordinateOutResolver(robotInfoService));
        resolverFactory.registerResolver(new AbormalWarnOutResolver());
        resolverFactory.registerResolver(new PatralTaskStatusOutResolver(patrolTaskService,patrolTaskExecService));
        resolverFactory.registerResolver(new StatusDataMessageOutResolver(robotInfoService));
        resolverFactory.registerResolver(new PatrolRouteOutResolver(sceneInfoService,robotInfoService,deviceInfoService));
        resolverFactory.registerResolver(new PatrolResultMessageOutResolver(patrolTaskResultService));
    }
}
