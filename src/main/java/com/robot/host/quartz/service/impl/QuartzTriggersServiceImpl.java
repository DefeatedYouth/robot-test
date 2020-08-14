package com.robot.host.quartz.service.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.robot.host.base.entry.DeviceInfoEntry;
import com.robot.host.base.entry.OperationLogEntity;
import com.robot.host.base.entry.PatrolTaskEntity;
import com.robot.host.base.service.DeviceInfoService;
import com.robot.host.base.service.OperationLogService;
import com.robot.host.base.service.PatrolTaskService;
import com.robot.host.base.vo.PageVO;
import com.robot.host.base.vo.Result;
import com.robot.host.base.vo.SchedulerJobVO;
import com.robot.host.common.constants.EnumTaskExecType;
import com.robot.host.common.constants.SysLogConstant;
import com.robot.host.common.util.ResultUtil;
import com.robot.host.quartz.constants.JobConstants;
import com.robot.host.quartz.dao.QuartzTriggersMapper;
import com.robot.host.quartz.entry.QuartzTriggers;
import com.robot.host.quartz.entry.ScheduleJobEntity;
import com.robot.host.quartz.service.QuartzTriggersService;
import com.robot.host.quartz.service.ScheduleJobService;
import com.robot.host.quartz.task.ScheduleJob;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service("quartzTriggersService")
public class QuartzTriggersServiceImpl extends ServiceImpl<QuartzTriggersMapper, QuartzTriggers> implements QuartzTriggersService {

    public static final String TASK_NAME = "TASK_";

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private ScheduleJobService scheduleJobService;

    @Autowired
    private PatrolTaskService patrolTaskService;

    @Autowired
    private DeviceInfoService deviceInfoService;

    @Autowired
    private QuartzTriggersMapper quartzTriggersMapper;


    /**
     * jobId 当前任务执行状况
     * @param jobId
     * @return
     */
    @Override
    public Result<SchedulerJobVO> getTaskStatusByTaskId(String  taskCode) {
        operationLogService.saveSysLogThenSendWebSocket(SysLogConstant.ROBOT_OTHER,
                SysLogConstant.SYS_LOCAL_STATUS,
                "获取任务执行状况，taskCode为：" + taskCode,
                null, null, null, this.getClass().getCanonicalName());
        SchedulerJobVO resultVO = new SchedulerJobVO();
        // 根据taskcode获取task
        PatrolTaskEntity patrolTask = patrolTaskService.getOne(new QueryWrapper<PatrolTaskEntity>().lambda().eq(PatrolTaskEntity::getPatrolTaskCode, taskCode));
        resultVO.setPatrolTaskEntity(patrolTask);
        //根据taskid获取所有子任务
        List<ScheduleJobEntity> jobs = quartzTriggersMapper.selectJobByPatrolId(patrolTask.getPatrolTaskId());

        List<Long> jobIds = jobs.stream().map(ScheduleJobEntity::getJobId).collect(Collectors.toList());

        List<String> taskNames = jobs.stream().map(job -> {
            return TASK_NAME + job.getJobId();
        }).collect(Collectors.toList());

        List<QuartzTriggers> triggers = this.list(new QueryWrapper<QuartzTriggers>().lambda().in(QuartzTriggers::getJobName, taskNames).orderByAsc(QuartzTriggers::getNextFireTime));
        QuartzTriggers trigger = triggers.isEmpty() ? new QuartzTriggers() : triggers.get(0);


        //TODO
        //获取schedulerJob
//        ScheduleJobEntity schedule = scheduleJobService.getById(Long.valueOf(jobId));
        //获取quartz_trigger  JobName Rule: TASK_NAME + jobId
//        QuartzTriggers trigger = this.getOne(new QueryWrapper<QuartzTriggers>().lambda().eq(QuartzTriggers::getJobName, TASK_NAME + jobId));
//        if(trigger == null){
            //如果trigger等于null，表示该任务已经执行完毕
//            trigger = new QuartzTriggers();
//        }
        //设置任务状态
        trigger.setStatus(jobs.get(0).getStatus());
        //查询巡检任务表
//        JSONObject params = JSONUtil.parseObj(schedule.getParams());
//        PatrolTaskEntity patrolTask = params.size() == 0 ? null : patrolTaskService.getById(Long.valueOf((Integer)params.get(JobConstants.QUARTZ_BUSI_ID)));
        resultVO.setQuartzTriggers(trigger);
        //获取任务执行日志
//        String cron = schedule.getCronExpression();
//        String[] crons = cron.split(" ");
        List<OperationLogEntity> logs = operationLogService.list(new QueryWrapper<OperationLogEntity>().lambda().in(OperationLogEntity::getJobId,jobIds).orderByDesc(OperationLogEntity::getOperationTime));
        if(!logs.isEmpty()){
//            if(crons.length == 7){
            String deviceId = logs.get(0).getDeviceId();
            resultVO.setCurrentDevice(StringUtils.isBlank(deviceId) ? null : deviceInfoService.getOne(new QueryWrapper<DeviceInfoEntry>().lambda().eq(DeviceInfoEntry::getDeviceId,deviceId)));
            resultVO.setLogs(logs);
//            }else{
//                resultVO.setLogs(logs.subList(0,logs.size() > 10 ? 10 : logs.size()));
//            }
        }
        return ResultUtil.data(resultVO);
    }

    /**
     *
     * @param pageVO
     * @return
     */
    @Override
    public PageInfo<QuartzTriggers> selectListByPage(PageVO pageVO) {
        PageHelper.startPage(pageVO.getPageNumber(), pageVO.getPageSize());
        List<QuartzTriggers> list = quartzTriggersMapper.selectTriggerListByPage(pageVO);
        PageInfo<QuartzTriggers> page = new PageInfo<>(list);
        operationLogService.saveSysLogThenSendWebSocket(SysLogConstant.ROBOT_OTHER,
                SysLogConstant.SYS_LOCAL_STATUS,
                "获取任务分页列表，当前页：" + pageVO.getPageNumber(),
                null, null, null, this.getClass().getCanonicalName());
        return page;
    }

    @Override
    public List<PatrolTaskEntity> selectListAll() {
        operationLogService.saveSysLogThenSendWebSocket(SysLogConstant.ROBOT_OTHER,
                SysLogConstant.SYS_LOCAL_STATUS,
                "获取巡检任务列表",
                null, null, null, this.getClass().getCanonicalName());
//        return quartzTriggersMapper.selectListAll();
        return patrolTaskService.list();
    }

    @Override
    public EnumTaskExecType[] getTaskStatus() {
        operationLogService.saveSysLogThenSendWebSocket(SysLogConstant.ROBOT_OTHER,
                SysLogConstant.SYS_LOCAL_STATUS,
                "获取任务执行状态下拉菜单",
                null, null, null, this.getClass().getCanonicalName());
        return EnumTaskExecType.values();
    }
}
