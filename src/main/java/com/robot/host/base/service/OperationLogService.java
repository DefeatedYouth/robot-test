package com.robot.host.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.robot.host.base.entry.OperationLogEntity;

public interface OperationLogService extends IService<OperationLogEntity> {

    OperationLogEntity saveSysLogThenSendWebSocket(Integer logType, Integer logStatus, String logContent, Long robotId, Long jobId, String deviceId, String className);
}
