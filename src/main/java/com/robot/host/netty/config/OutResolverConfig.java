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

    @Autowired
    private OperationLogService operationLogService;

    public void init(){
        initRosolverFactory(resolverFactory);
    }


    public void initRosolverFactory(MessageResolverOutFactory resolverFactory) {
        resolverFactory.registerResolver(new RunDataMessageOutResolver(operationLogService));
        resolverFactory.registerResolver(new WeatherDataMessageOutResolver(operationLogService));
        resolverFactory.registerResolver(new CoordinateOutResolver(robotInfoService, operationLogService));
        resolverFactory.registerResolver(new AbormalWarnOutResolver(operationLogService));
        resolverFactory.registerResolver(new PatralTaskStatusOutResolver(patrolTaskService,patrolTaskExecService, operationLogService));
        resolverFactory.registerResolver(new StatusDataMessageOutResolver(robotInfoService, operationLogService));
        resolverFactory.registerResolver(new PatrolRouteOutResolver(sceneInfoService,robotInfoService,deviceInfoService, operationLogService));
        resolverFactory.registerResolver(new PatrolResultMessageOutResolver(patrolTaskResultService, operationLogService));
    }
}
