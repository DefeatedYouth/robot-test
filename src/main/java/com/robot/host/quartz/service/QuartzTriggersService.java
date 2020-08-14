package com.robot.host.quartz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.robot.host.base.entry.PatrolTaskEntity;
import com.robot.host.base.vo.PageVO;
import com.robot.host.base.vo.Result;
import com.robot.host.base.vo.SchedulerJobVO;
import com.robot.host.common.constants.EnumTaskExecType;
import com.robot.host.quartz.entry.QuartzTriggers;
import com.robot.host.quartz.entry.ScheduleJobEntity;

import java.util.List;

public interface QuartzTriggersService extends IService<QuartzTriggers> {
    Result<SchedulerJobVO> getTaskStatusByTaskId(String taskCode);

    PageInfo<QuartzTriggers> selectListByPage(PageVO pageVO);

    List<PatrolTaskEntity> selectListAll();

    EnumTaskExecType[] getTaskStatus();
}
