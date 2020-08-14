package com.robot.host.base.controller;

import com.github.pagehelper.PageInfo;
import com.robot.host.base.entry.PatrolTaskEntity;
import com.robot.host.base.service.OperationLogService;
import com.robot.host.base.service.RobotCommonService;
import com.robot.host.base.vo.PageVO;
import com.robot.host.base.vo.Result;
import com.robot.host.base.vo.SchedulerJobVO;
import com.robot.host.common.constants.EnumTaskExecType;
import com.robot.host.common.constants.SysLogConstant;
import com.robot.host.common.util.ResultUtil;
import com.robot.host.quartz.entry.QuartzTriggers;
import com.robot.host.quartz.entry.ScheduleJobEntity;
import com.robot.host.quartz.service.QuartzTriggersService;
import com.robot.host.quartz.service.ScheduleJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.testng.collections.Lists;

import java.util.List;

@RestController
@RequestMapping("/message/task")
public class ScheduleJobController {

    @Autowired
    private QuartzTriggersService quartzTriggersService;

    @Autowired
    private ScheduleJobService scheduleJobService;

    @Autowired
    private OperationLogService operationLogService;

    /**
     * 分页查询任务列表
     * @return
     */
    @PostMapping("/listByPage")
    public Result<PageInfo<QuartzTriggers>> selectListByPage(@RequestBody PageVO pageVO){
        PageInfo<QuartzTriggers> schedulerList = quartzTriggersService.selectListByPage(pageVO);
        return new Result<PageInfo<QuartzTriggers>>().setData(schedulerList,"任务查询成功");
    }


    /**
     * 根据taskId 获取任务执行状态
     * @param jobId
     * @return
     */
    @GetMapping("/getOne/{taskCode}")
    public Result<SchedulerJobVO> getTaskStatusByTaskId(@PathVariable("taskCode")String taskCode){
        Result<SchedulerJobVO> result = quartzTriggersService.getTaskStatusByTaskId(taskCode);
        return result;
    }


    /**
     * 获取所有任务
     * @return
     */
    @GetMapping("/getAll")
    public Result<List<PatrolTaskEntity>> getAll(){
        return ResultUtil.data(quartzTriggersService.selectListAll());
    }


    @GetMapping("/status")
    public Result<List<EnumTaskExecType>> getTaskStatus(){
        return ResultUtil.data(Lists.newArrayList(quartzTriggersService.getTaskStatus()));
    }

}
