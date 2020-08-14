package com.robot.host.base.entry;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("robot_patrol_task_result")
public class PatrolTaskResultEntity {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long patrolTaskResultId;

    public String robotCode;

    public String taskName;

    public String taskCode;

    public String deviceName;

    public Long deviceId;

    public String value;

    public String unit;

    public Date time;

    public String recognitionType;

    public Integer fileType;

    public String filePath;

    public String rectangle;

    public Long taskPatrolledId;
}
