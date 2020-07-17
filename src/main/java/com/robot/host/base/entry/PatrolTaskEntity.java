package com.robot.host.base.entry;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.robot.host.common.constants.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 巡检任务表
 *
 * @author dadi
 * @email
 * @date 2020-04-27 19:37:47
 */
@Data
@TableName("robot_patrol_task")
@ApiModel(" 巡检任务表")
public class PatrolTaskEntity extends SiteBaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 任务id
     */

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "任务id", dataType = "Long")
    private Long patrolTaskId;
    /**
     * 任务名称
     */

    @ApiModelProperty(value = "任务名称", dataType = "String")
    private String patrolTaskName;
    @ApiModelProperty(value = "任务编号", dataType = "String")
    private String patrolTaskCode;
    /**
     * 任务类型(（实时任务、定期任务、周期任务）)
     */
    @ApiModelProperty(value = "任务类型(（1实时任务、2定时任务、3周期任务）)", dataType = "Integer")
    @NotNull(message = "任务类型不能为空", groups = {AddGroup.class})
    private Integer patrolRuleType;
    @TableField(exist = false)
    private String patrolRuleTypeName;
    /**
     * 巡检类型(例行巡检、全面巡检、特殊巡检、熄灯巡检、专项巡检、自定义巡检)
     */
    @ApiModelProperty(value = "巡检类型(2例行巡检、1全面巡检、4特殊巡检、5熄灯巡检、3专项巡检、6自定义巡检)", dataType = "Integer")
    @NotNull(message = "巡检类型不能为空", groups = {AddGroup.class})
    private Integer patrolType;
    @TableField(exist = false)
    private String patrolTypeName;
    @ApiModelProperty(value = "巡检类型数量", dataType = "Integer")
    @TableField(exist = false)
    private Integer patrolNum;
    /**
     * 计划开始时间
     */

    @ApiModelProperty(value = "计划开始时间", dataType = "Date")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date taskBeginTime;
    /**
     * 计划结束时间
     */

    @ApiModelProperty(value = "计划结束时间", dataType = "Date")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date taskEndTime;
    /**
     * 实际开始时间
     */

    @ApiModelProperty(value = "实际开始时间", dataType = "Date")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date realBeginTime;
    /**
     * 实际结束时间
     */

    @ApiModelProperty(value = "实际结束时间", dataType = "Date")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date realEndTime;
    /**
     * 审核状态(待审核，通过，不通过)
     */

    @ApiModelProperty(value = "审核状态(0待审核，1通过，-1不通过)", dataType = "Integer")
    private Integer auditStatus;
    @TableField(exist = false)
    private String auditStatusName;
    /**
     * 执行状态（未开始，周期中，进行中，已完成）
     */

    @ApiModelProperty(value = "执行状态（0未开始，1周期中，2进行中，3已完成,4终止，5暂停）", dataType = "Integer")
    private Integer execStatus;
    @TableField(exist = false)
    private String execStatusName;

    @ApiModelProperty(value = "进行修改时，之前的状态", dataType = "Integer")
    @TableField(exist = false)
    private Integer oldexecStatus;

    /**
     * 0:周，1月周期
     */

    @ApiModelProperty(value = "间隔类型1:小时，2：天，3:周，4月，5定时", dataType = "Integer")
    @NotNull(message = "间隔类型不能为空", groups = {AddGroup.class})
    private Integer periodType;
    @TableField(exist = false)
    private String periodTypeName;

    /**
     * 天间隔，逗号分隔1,2,3
     */

    @ApiModelProperty(value = "天间隔，逗号分隔1,2,3", dataType = "String")
    private String periodVal;
    @TableField(exist = false)
    private String[] periodValArr;
    @ApiModelProperty(value = "图片巡检路线图", dataType = "String")
    private String patrolResultPic;
    /**
     * 12:15:15
     */
    @ApiModelProperty(value = "执行时间", dataType = "String")
    private String periodExecuteTime;
    @ApiModelProperty(value = "定时时间", dataType = "String")
    private String fixedTime;
    /**
     * 12:15:15
     */
    @ApiModelProperty(value = "任务表的id", dataType = "String")
    private Long scheduleId;
    @ApiModelProperty(value = "cron表达式", dataType = "String")
    private String cron;
    @ApiModelProperty(value = "优先级", dataType = "Integer")
    private Integer priority;
    @ApiModelProperty(value = "场景列表", dataType = "String")
    @TableField(exist = false)
    List<SceneInfoEntity> subSceneList;




    public String getPeriodVal() {
        return periodVal;
    }

    public void setPeriodVal(String periodVal) {
        this.periodVal = periodVal;

    }

    public String[] getPeriodValArr() {
        if (periodValArr == null && StringUtils.isNotBlank(periodVal)) {
            periodValArr = periodVal.split(",");
        }
        return periodValArr;
    }

    public void setPeriodValArr(String[] periodValArr) {
        if (periodValArr != null) {
            this.periodVal = StringUtils.join(periodValArr, ",");
        }
        this.periodValArr = periodValArr;
    }

    public String getPatrolRuleTypeName() {
        if (patrolRuleType != null) {
            EnumPatrolRuleType enumVal = EnumPatrolRuleType.valueOf(patrolRuleType);
            return enumVal == null ? "" : enumVal.getText();
        }
        return patrolRuleTypeName;
    }

    public String getPatrolTypeName() {
        if (patrolType != null) {
            EnumPatrolType enumVal = EnumPatrolType.valueOf(patrolType);
            return enumVal == null ? "" : enumVal.getText();
        }
        return patrolTypeName;
    }

    public void setPatrolTypeName(String patrolTypeName) {
        this.patrolTypeName = patrolTypeName;
    }

    public void setPatrolRuleTypeName(String patrolRuleTypeName) {
        this.patrolRuleTypeName = patrolRuleTypeName;
    }

    public String getAuditStatusName() {
        if (auditStatus != null) {
            EnumPatrolAuditStatus enumVal = EnumPatrolAuditStatus.valueOf(auditStatus);
            return enumVal == null ? "" : enumVal.getText();
        }
        return auditStatusName;
    }

    public void setAuditStatusName(String auditStatusName) {
        this.auditStatusName = auditStatusName;
    }

    public String getExecStatusName() {
        if (execStatus != null) {
            EnumPatrolExecStatus enumVal = EnumPatrolExecStatus.valueOf(execStatus);
            return enumVal == null ? "" : enumVal.getText();
        }
        return execStatusName;
    }

    public void setExecStatusName(String execStatusName) {
        this.execStatusName = execStatusName;
    }

    public String getPeriodTypeName() {
        if (periodType != null) {
            EnumTaskPeriodType enumVal = EnumTaskPeriodType.valueOf(periodType);
            return enumVal == null ? "" : enumVal.getText();
        }
        return periodTypeName;
    }

    public void setPeriodTypeName(String periodTypeName) {
        this.periodTypeName = periodTypeName;
    }
}
