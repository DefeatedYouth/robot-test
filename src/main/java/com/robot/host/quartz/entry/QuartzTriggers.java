package com.robot.host.quartz.entry;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("QRTZ_TRIGGERS")
@Data
public class QuartzTriggers {
    private String jobName;

    private Long  nextFireTime;

    private Long prevFireTime;

    private Integer priority;

    private String triggerState;

    private Long startTime;

    private Long endTime;
}
