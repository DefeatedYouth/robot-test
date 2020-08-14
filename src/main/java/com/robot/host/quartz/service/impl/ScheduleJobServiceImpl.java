/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.robot.host.quartz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.robot.host.common.constants.Constant;
import com.robot.host.common.constants.EnumSysConfigType;
import com.robot.host.common.util.QuartzUtil;
import com.robot.host.common.util.SysConfigUtil;
import com.robot.host.quartz.dao.ScheduleJobDao;
import com.robot.host.quartz.entry.ScheduleJobEntity;
import com.robot.host.quartz.service.ScheduleJobService;
import com.robot.host.quartz.util.ScheduleUtils;
import org.quartz.CronTrigger;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.testng.collections.Lists;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service("scheduleJobService")
public class ScheduleJobServiceImpl extends ServiceImpl<ScheduleJobDao, ScheduleJobEntity> implements ScheduleJobService {
	@Autowired
    private Scheduler scheduler;

	@Autowired
	private ScheduleJobService scheduleJobService;
	
	/**
	 * 项目启动时，初始化定时器
	 */
	public void init(){
		List<ScheduleJobEntity> scheduleJobList = this.list();
		for(ScheduleJobEntity scheduleJob : scheduleJobList){
			//设置运行数据和微气象的间隔
			switch (scheduleJob.getBeanName()){
				case "runDataTask":
					scheduleJob.setCronExpression(QuartzUtil.getCronBySeconds(Long.valueOf(SysConfigUtil.get(EnumSysConfigType.RunData.getName()))));
					scheduleJobService.saveOrUpdate(scheduleJob);
					break;
				case "weatherDataTask":
					scheduleJob.setCronExpression(QuartzUtil.getCronBySeconds(Long.valueOf(SysConfigUtil.get(EnumSysConfigType.WeatherData.getName()))));
					scheduleJobService.saveOrUpdate(scheduleJob);
					break;
			}
			//跳过过期任务
			String crons = scheduleJob.getCronExpression();
			if(crons.split(" ").length  == 7){
				try {
					Date startDate = new SimpleDateFormat("ss mm HH dd MM ? yyyy").parse(crons);
					if(startDate.before(new Date())){
						scheduleJob.setStatus(1);
						this.saveOrUpdate(scheduleJob);
						continue;
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			CronTrigger cronTrigger = ScheduleUtils.getCronTrigger(scheduler, scheduleJob.getJobId());
            //如果不存在，则创建
            if(cronTrigger == null) {
                ScheduleUtils.createScheduleJob(scheduler, scheduleJob);
            }else {
                ScheduleUtils.updateScheduleJob(scheduler, scheduleJob);
            }
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveJob(ScheduleJobEntity scheduleJob) {
		scheduleJob.setCreateTime(new Date());
		scheduleJob.setStatus(Constant.ScheduleStatus.NORMAL.getValue());
        this.save(scheduleJob);
        
        ScheduleUtils.createScheduleJob(scheduler, scheduleJob);
    }
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void update(ScheduleJobEntity scheduleJob) {
        ScheduleUtils.updateScheduleJob(scheduler, scheduleJob);
                
        this.updateById(scheduleJob);
    }

	@Override
	@Transactional(rollbackFor = Exception.class)
    public void deleteBatch(Long[] jobIds) {
    	for(Long jobId : jobIds){
    		ScheduleUtils.deleteScheduleJob(scheduler, jobId);
    	}
    	
    	//删除数据
    	this.removeByIds(Arrays.asList(jobIds));
	}

	@Override
    public int updateBatch(Long[] jobIds, int status){
    	Map<String, Object> map = new HashMap<>(2);
    	map.put("list", Lists.newArrayList(jobIds));
    	map.put("status", status);
    	return baseMapper.updateBatch(map);
    }
    
	@Override
	@Transactional(rollbackFor = Exception.class)
    public void run(Long[] jobIds) {
    	for(Long jobId : jobIds){
    		ScheduleUtils.run(scheduler, this.getById(jobId));
    	}
    }

	@Override
	@Transactional(rollbackFor = Exception.class)
    public void pause(Long[] jobIds) {
        for(Long jobId : jobIds){
    		ScheduleUtils.pauseJob(scheduler, jobId);
    	}
        
    	updateBatch(jobIds, Constant.ScheduleStatus.PAUSE.getValue());
    }

	@Override
	@Transactional(rollbackFor = Exception.class)
    public void resume(Long[] jobIds) {
    	for(Long jobId : jobIds){
    		ScheduleUtils.resumeJob(scheduler, jobId);
    	}

    	updateBatch(jobIds, Constant.ScheduleStatus.NORMAL.getValue());
    }



}
