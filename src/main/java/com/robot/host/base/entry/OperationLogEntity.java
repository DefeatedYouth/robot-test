package com.robot.host.base.entry;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;

@TableName("robot_operation_log")
@Data
@ApiModel("操作日志")
public class OperationLogEntity {

    /**
     * 操作日志id
     */
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "操作日志id", dataType = "Long")
    private Long operationLogId;

    /**
     * 日志类型  1、机器人控制    2、巡检任务
     */
    @ApiModelProperty(value = "日志类型", dataType = "Integer")
    private Integer logType;


    /**
     * 日志状态 0 异常  1 输入  2  输出
     */
    private Integer logStatus;

    /**
     * 机器人id
     */
    @ApiModelProperty(value = "机器人id", dataType = "Long")
    private Long robotId;

    /**
     * 任务id
     */
    @ApiModelProperty(value = "任务id", dataType = "Long")
    private Long jobId;

    /**
     * 操作时间
     */
    @ApiModelProperty(value = "操作时间")
    private Long operationTime;

    /**
     * 操作内容
     */
    @ApiModelProperty(value = "操作内容", dataType = "String")
    private String logContent;

    /**
     *
     */
    private String className;

    /**
     * 场景id
     */
    private String deviceId;
}
