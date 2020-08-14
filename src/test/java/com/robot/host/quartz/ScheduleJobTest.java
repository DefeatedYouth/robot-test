package com.robot.host.quartz;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.robot.host.common.util.QuartzUtil;
import com.robot.host.quartz.entry.ScheduleJobEntity;
import com.robot.host.quartz.service.ScheduleJobService;
import com.robot.host.quartz.util.ScheduleUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.CronTrigger;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Set;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@Slf4j
public class ScheduleJobTest {


    @Autowired
    private Scheduler scheduler;

    @Test
    public void list() throws Exception{
        Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.anyGroup());
        jobKeys.forEach( key -> {
            System.out.println(key);
        });
    }

    @Autowired
    private ScheduleJobService scheduleJobService;

    @Test
    public void initCron(){
        System.out.println(QuartzUtil.getCronBySeconds(90L));
    }

    @Test
    public void getJobs(){
        List<ScheduleJobEntity> scheduleJobs = scheduleJobService.list(new QueryWrapper<ScheduleJobEntity>().lambda().eq(ScheduleJobEntity::getBeanName, "runDataTask").or().eq(ScheduleJobEntity::getBeanName, "weatherDataTask"));
        for (ScheduleJobEntity scheduleJob : scheduleJobs) {
            if(scheduleJob.getBeanName().equalsIgnoreCase("runDataTask")){
                scheduleJob.setCronExpression(QuartzUtil.getCronBySeconds(180L));
            }else{
                scheduleJob.setCronExpression(QuartzUtil.getCronBySeconds(120L));
            }
            CronTrigger cronTrigger = ScheduleUtils.getCronTrigger(scheduler, scheduleJob.getJobId());
            //如果不存在，则创建
            if(cronTrigger == null) {
                ScheduleUtils.createScheduleJob(scheduler, scheduleJob);
            }else {
                log.info("修改{}时间", scheduleJob.getBeanName());
                ScheduleUtils.updateScheduleJob(scheduler, scheduleJob);
            }
        }
        scheduleJobs.forEach(System.out::println);
    }
}
