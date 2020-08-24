package com.robot.host.netty.config;

import com.robot.host.base.service.*;
import com.robot.host.common.util.FTPRobotUtils;
import com.robot.host.netty.resolver.out.*;
import com.robot.host.quartz.dao.ScheduleJobDao;
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

    @Autowired
    private ScheduleJobDao scheduleJobDao;

    @Autowired
    private FTPRobotUtils ftpRobotUtils;

    public void init(){
        initRosolverFactory(resolverFactory);
    }


    public void initRosolverFactory(MessageResolverOutFactory resolverFactory) {
        resolverFactory.registerResolver(new RunDataMessageOutResolver(operationLogService));
        resolverFactory.registerResolver(new WeatherDataMessageOutResolver(operationLogService));
        resolverFactory.registerResolver(new CoordinateOutResolver(robotInfoService, operationLogService, ftpRobotUtils));
        resolverFactory.registerResolver(new AbormalWarnOutResolver(operationLogService));
        resolverFactory.registerResolver(new PatralTaskStatusOutResolver(patrolTaskService,patrolTaskExecService, operationLogService, scheduleJobDao));
        resolverFactory.registerResolver(new StatusDataMessageOutResolver(robotInfoService, operationLogService));
        resolverFactory.registerResolver(new PatrolRouteOutResolver(sceneInfoService,robotInfoService,deviceInfoService, operationLogService, ftpRobotUtils));
        resolverFactory.registerResolver(new PatrolResultMessageOutResolver(patrolTaskResultService, operationLogService));
    }
}
