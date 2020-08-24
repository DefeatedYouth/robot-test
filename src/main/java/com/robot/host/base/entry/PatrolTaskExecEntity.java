package com.robot.host.base.entry;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 巡检任务执行情况列表
 *
 * @author dadi
 * @email
 * @date 2020-05-24 15:35:05
 */
@Data
@TableName("robot_patrol_task_exec")
@ApiModel(" 巡检任务执行情况列表")
public class PatrolTaskExecEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 任务执行id
     */

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "任务执行id", dataType = "Long")
    private Long patrolTaskExecId;
    /**
     * 任务id
     */
    @ApiModelProperty(value = "任务id", dataType = "Long")
    private Long patrolTaskId;
    /**
     * 巡检编号
     */

    @ApiModelProperty(value = "巡检编号", dataType = "String")
    private String patrolTaskCode;
    /**
     * 任务名称
     */

    @ApiModelProperty(value = "任务名称", dataType = "String")
    private String patrolTaskName;
    /**
     * 任务类型(（实时任务、定期任务、周期任务）)
     */

    @ApiModelProperty(value = "任务类型(（实时任务、定期任务、周期任务）)", dataType = "Integer")
    private Integer patrolRuleType;
    /**
     * 巡检类型(2例行巡检、1全面巡检、4特殊巡检、5熄灯巡检、3专项巡检、6自定义巡检)
     */

    @ApiModelProperty(value = "巡检类型(2例行巡检、1全面巡检、4特殊巡检、5熄灯巡检、3专项巡检、6自定义巡检)", dataType = "Integer")
    private Integer patrolType;
    /**
     * 执行状态（0未开始，1周期中，2进行中，3已完成，4终止，5暂停）
     */

    @ApiModelProperty(value = "执行状态（0未开始，1周期中，2进行中，3已完成，4终止，5暂停）", dataType = "Integer")
    private Integer execStatus;


    @ApiModelProperty(value = "0未开始，1已经结束", dataType = "Integer")
    private Integer runStatus;
    /**
     * 变电站id
     */

    @ApiModelProperty(value = "变电站id", dataType = "String")
    private String siteId;
    /**
     * 执行时间
     */

    @ApiModelProperty(value = "执行时间", dataType = "Date")
    private Date createTime;

}
