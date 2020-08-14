package com.robot.host.quartz.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.robot.host.base.vo.PageVO;
import com.robot.host.quartz.entry.QuartzTriggers;
import com.robot.host.quartz.entry.ScheduleJobEntity;
import com.robot.host.quartz.task.ScheduleJob;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface QuartzTriggersMapper extends BaseMapper<QuartzTriggers> {

    List<QuartzTriggers> selectTriggerListByPage(PageVO pageVO);

    List<ScheduleJobEntity> selectListAll();

    List<ScheduleJobEntity> selectJobByPatrolId(@Param("patrolTaskId") Long patrolTaskId);
}
