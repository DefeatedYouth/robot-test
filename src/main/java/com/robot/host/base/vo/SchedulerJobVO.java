package com.robot.host.base.vo;


import com.robot.host.base.entry.DeviceInfoEntry;
import com.robot.host.base.entry.OperationLogEntity;
import com.robot.host.base.entry.PatrolTaskEntity;
import com.robot.host.quartz.entry.QuartzTriggers;
import com.robot.host.quartz.entry.ScheduleJobLogEntity;
import lombok.Data;

import java.util.List;

/**
 * 任务执行状态
 */
@Data
public class SchedulerJobVO {

    private QuartzTriggers quartzTriggers;

    private PatrolTaskEntity patrolTaskEntity;

    private DeviceInfoEntry currentDevice;


    private List<OperationLogEntity> logs;

}
