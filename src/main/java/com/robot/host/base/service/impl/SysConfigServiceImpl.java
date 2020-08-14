package com.robot.host.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.robot.host.base.entry.SysConfig;
import com.robot.host.base.mapper.SysConfigMapper;
import com.robot.host.base.service.OperationLogService;
import com.robot.host.base.service.SysConfigService;
import com.robot.host.common.constants.EnumSysConfigType;
import com.robot.host.common.constants.SysLogConstant;
import com.robot.host.common.util.QuartzUtil;
import com.robot.host.common.util.SysConfigUtil;
import com.robot.host.quartz.entry.ScheduleJobEntity;
import com.robot.host.quartz.service.ScheduleJobService;
import com.robot.host.quartz.util.ScheduleUtils;
import org.quartz.CronTrigger;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

@Service("sysConfigService")
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements SysConfigService {


    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private ScheduleJobService scheduleJobService;

    @Autowired
    private Scheduler scheduler;

    /**
     * 缓存数据库信息到本地
     */
    public void init(){
        List<SysConfig> list = this.list();
        list.forEach(sysConfig -> {
            SysConfigUtil.put(sysConfig.getConfigName(), sysConfig.getConfigValue());
        });
    }

    /**
     * 获取系统配置信息
     * @return
     */
    @Override
    public List<SysConfig> sysConfigList() {
        operationLogService.saveSysLogThenSendWebSocket(SysLogConstant.ROBOT_OTHER,
                SysLogConstant.SYS_LOCAL_STATUS,
                "查看系统配置信息",
                null, null, null, this.getClass().getCanonicalName());
        return this.list();
    }

    @Override
    @Transactional
    public void updateSysConfig(SysConfig sysConfig, StringBuilder returnMessage) {
        Class clazz = null;
        try {
            String name = sysConfig.getConfigName();
            String value = sysConfig.getConfigValue();
            EnumSysConfigType sysConfigType = EnumSysConfigType.getEnum(name);
            clazz = sysConfigType.getClazz();
            if(clazz != value.getClass()){
                Method valueOf = clazz.getMethod("valueOf", String.class);
                Object invoke = valueOf.invoke(clazz, value);
                if(invoke != null){
                    String beanName = sysConfigType.getBeanName();
                    if(beanName != null){
                        this.updateScheduleJob(beanName, invoke);
                    }
                }
                SysConfigUtil.put(sysConfigType.getName(),value);
            }
            operationLogService.saveSysLogThenSendWebSocket(SysLogConstant.ROBOT_OTHER,
                    SysLogConstant.SYS_LOCAL_STATUS,
                    "系统配置信息修改成功",
                    null, null, null, this.getClass().getCanonicalName());
            this.saveOrUpdate(sysConfig);
        } catch (Exception e) {
            returnMessage.append("系统配置信息修改失败：类型错误，期望的类型为：" + clazz.getSimpleName());
        }
    }

    /**
     * 修改运行数据和微气象数据的运行间隔
     * @param beanName
     * @param value
     */
    @Transactional
    private void updateScheduleJob(String beanName, Object value) {
        ScheduleJobEntity scheduleJob = scheduleJobService.getOne(new QueryWrapper<ScheduleJobEntity>().lambda().eq(ScheduleJobEntity::getBeanName, beanName));
        scheduleJob.setCronExpression(QuartzUtil.getCronBySeconds((Long) value));
        CronTrigger cronTrigger = ScheduleUtils.getCronTrigger(scheduler, scheduleJob.getJobId());
        //如果不存在，则创建
        if(cronTrigger == null) {
            ScheduleUtils.createScheduleJob(scheduler, scheduleJob);
        }else {
            ScheduleUtils.updateScheduleJob(scheduler, scheduleJob);
        }
        scheduleJobService.saveOrUpdate(scheduleJob);
    }
}
