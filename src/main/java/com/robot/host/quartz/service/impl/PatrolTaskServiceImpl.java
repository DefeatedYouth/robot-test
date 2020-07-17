//package com.robot.host.quartz.service.impl;
//
//import com.robot.host.common.config.constants.SmartConstants;
//import com.robot.host.quartz.service.PatrolTaskService;
//import com.robot.host.common.util.RedisTemplateHelper;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//@Service("patrolTaskService")
//@Slf4j
//public class PatrolTaskServiceImpl implements PatrolTaskService {
//
//
//    @Autowired
//    private RedisTemplateHelper redisTemplateHelper;
//
//    @Override
//    public String getPatrolTaskSceneFromSet(Long busiId, Long execId) {
//        return redisTemplateHelper.get(SmartConstants.redis_key_patrol_task_robot_set,busiId + "_" + execId);
//    }
//}
