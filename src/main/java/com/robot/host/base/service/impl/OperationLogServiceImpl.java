package com.robot.host.base.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.robot.host.base.entry.OperationLogEntity;
import com.robot.host.base.mapper.OperationLogMapper;
import com.robot.host.base.service.OperationLogService;
import com.robot.host.common.constants.EnumTopicDistination;
import com.robot.host.common.constants.SysLogConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service("operationLogService")
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLogEntity> implements OperationLogService {


    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     *
     * @param logType
     * @param logStatus
     * @param logContentStatus
     * @param logContent
     * @param robotId
     * @param jobId
     * @param deviceId
     * @param className
     */
    @Override
    public OperationLogEntity saveSysLogThenSendWebSocket(Integer logType, Integer logStatus, String logContent, Long robotId, Long jobId, String deviceId, String className) {
        long currentTime = System.currentTimeMillis();
        //添加系统日志
        OperationLogEntity log = new OperationLogEntity();
        log.setLogType(logType);
        log.setLogStatus(logStatus);
        log.setRobotId(robotId);
        log.setJobId(jobId);
        log.setOperationTime(currentTime);
        log.setDeviceId(deviceId);
//        String logPrefix = String.format(SysLogConstant.SYS_LOG_PREFIX,SysLogConstant.LOG_DATE_FORMAT.format(currentTime), logContentStatus, className);
        log.setClassName(className);
        log.setLogContent(logContent);
        this.save(log);

        //发送给浏览器
        messagingTemplate.convertAndSend(EnumTopicDistination.SYS_LOG.getText(),log);

        return log;
    }
}
