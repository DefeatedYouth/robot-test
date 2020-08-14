package com.robot.host.quartz.entry;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.beans.Transient;
import java.util.Date;

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


    /**
     * 巡检任务名称
     */
    @TableField(exist = false)
    private String patrolTaskName;

    @TableField(exist = false)
    private Date execTime;

    @TableField(exist = false)
    private Date createTime;


    /**
     * 任务状态
     */
    @TableField(exist = false)
    private Integer status;

    @Transient
    public String getTriggerStatusValue(){
        String value = "";
        switch (triggerState){
            case "WAITING": value = "等待";break;
            case "PAUSED": value = "暂停";break;
            case "ACQUIRED": value = "正常执行";break;
            case "BLOCKED": value = "阻塞";break;
            case "ERROR": value = "错误";break;
        }
        return value;
    }
}
