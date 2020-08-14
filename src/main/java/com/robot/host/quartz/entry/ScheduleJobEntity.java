/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.robot.host.quartz.entry;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.robot.host.common.constants.EnumSysConfigType;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

/**
 * 定时任务
 *
 * @author Mark sunlightcs@gmail.com
 */
@Data
@TableName("schedule_job")
public class ScheduleJobEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 任务调度参数key
	 */
    public static final String JOB_PARAM_KEY = "JOB_PARAM_KEY";
	
	/**
	 * 任务id
	 */
	@TableId(type = IdType.AUTO)
	private Long jobId;

	/**
	 * spring bean名称
	 */
	@NotBlank(message="bean名称不能为空")
	private String beanName;
	
	/**
	 * 参数
	 */
	private String params;
	
	/**
	 * cron表达式
	 */
	@NotBlank(message="cron表达式不能为空")
	private String cronExpression;

	/**
	 * 任务状态
	 */
	private Integer status;

	/**
	 * 备注
	 */
	private String remark;

	/**
	 * 创建时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;

	/**
	 * 开始时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")

	@TableField(exist = false)
	private Date startTime;
	/**
	 * 结束时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@TableField(exist = false)
	private Date endTime;

	/**
	 * 1定时任务，2周期任务
	 */
	@TableField(exist = false)
	private Integer scheduleType;

	/**
	 * 巡检任务名称
	 */
	@TableField(exist = false)
	private String patrolTaskName;

	private void setPatrolTaskNameByParams(){
		if (params == null){
			if(this.beanName.equalsIgnoreCase(EnumSysConfigType.RunData.getBeanName())){
				this.setPatrolTaskName("运行数据");
			}
			if(this.beanName.equalsIgnoreCase(EnumSysConfigType.RunData.getBeanName())){
				this.setPatrolTaskName("微气象数据");
			}
		}
	}

}
