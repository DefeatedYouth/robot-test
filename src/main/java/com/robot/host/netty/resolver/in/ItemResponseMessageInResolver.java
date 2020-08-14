package com.robot.host.netty.resolver.in;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.robot.host.base.entry.SysConfig;
import com.robot.host.base.service.OperationLogService;
import com.robot.host.base.service.SysConfigService;
import com.robot.host.common.constants.EnumSysConfigType;
import com.robot.host.common.constants.NettyConstants;
import com.robot.host.common.constants.ProtocolMessage;
import com.robot.host.common.constants.SysLogConstant;
import com.robot.host.common.dto.MessageJudgeInDTO;
import com.robot.host.common.dto.RegisterInXmlDTO;
import com.robot.host.common.util.QuartzUtil;
import com.robot.host.common.util.SessionSocketHolder;
import com.robot.host.common.util.SysConfigUtil;
import com.robot.host.common.util.XmlBeanUtils;
import com.robot.host.quartz.entry.ScheduleJobEntity;
import com.robot.host.quartz.service.ScheduleJobService;
import com.robot.host.quartz.util.ScheduleUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.CronTrigger;
import org.quartz.Scheduler;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
public class ItemResponseMessageInResolver implements InResolver{

    private SysConfigService sysConfigService;

    private Scheduler scheduler;

    private ScheduleJobService scheduleJobService;

    private OperationLogService operationLogService;

    public ItemResponseMessageInResolver(SysConfigService sysConfigService, ScheduleJobService scheduleJobService, OperationLogService operationLogService, Scheduler scheduler) {
        this.sysConfigService = sysConfigService;
        this.scheduleJobService = scheduleJobService;
        this.operationLogService = operationLogService;
        this.scheduler = scheduler;
    }

    @Override
    public boolean support(MessageJudgeInDTO judgeInDTO) {
        if (!StringUtils.equals(judgeInDTO.getType(), NettyConstants.IN_OUT_CODE_SYSTEM_TYPE + "")) {
            return false;
        }
        if (!StringUtils.equals(judgeInDTO.getCommand(), NettyConstants.OUT_CODE_SYSTEM_TYPE_COMMAND_COMMON_ITEMS + "")) {
            return false;
        }
        return true;
    }

    @Override
    public ProtocolMessage resolve(ProtocolMessage message, ChannelHandlerContext ctx) {
        log.info("item响应:{}",message.getBody());

        //注册成功，添加channel信息
        SessionSocketHolder.putClient((NioSocketChannel) ctx.channel());
        SessionSocketHolder.put(NettyConstants.ROBOT_HOST_CODE, (NioSocketChannel)ctx.channel());

        RegisterInXmlDTO registerResp = XmlBeanUtils.xmlToBean(message.getBody(), RegisterInXmlDTO.class);
        //获取心跳间隔、机器人运行数据间隔、微气象数据间隔
        RegisterInXmlDTO.Item registerItem = registerResp.getItems().get(0);
        //修改系统配置参数值、任务间隔
        //心跳
        updateSysConfigValueAndScheduleJob(registerItem.getHeartBeatInterval(), EnumSysConfigType.Heart, false);
        //运行间隔
        updateSysConfigValueAndScheduleJob(registerItem.getRobotRunInterval(), EnumSysConfigType.RunData, true);
        //微气象间隔
        updateSysConfigValueAndScheduleJob(registerItem.getWeatherInterval(), EnumSysConfigType.WeatherData, true);
        //添加系统操作日志
        operationLogService.saveSysLogThenSendWebSocket(SysLogConstant.ROBOT_OTHER,
                SysLogConstant.SYS_LOCAL_STATUS,
                String.format("[%s]注册成功", operationName()),
                null, null, null ,className());

        return null;
    }

    /**
     * 修改系统配置参数值、任务间隔
     * @param registerItem
     */
    @Transactional
    private void updateSysConfigValueAndScheduleJob(String value, EnumSysConfigType sysConfigType, boolean flag) {
        SysConfig sysConfig = sysConfigService.getOne(new QueryWrapper<SysConfig>().lambda().eq(SysConfig::getConfigName, sysConfigType.getName()));
        sysConfig.setConfigValue(value);
        sysConfigService.saveOrUpdate(sysConfig);
        SysConfigUtil.put(sysConfigType.getName(), value);
        if(flag){
            ScheduleJobEntity scheduleJob = scheduleJobService.getOne(new QueryWrapper<ScheduleJobEntity>().lambda().eq(ScheduleJobEntity::getBeanName, sysConfigType.getBeanName()));
            scheduleJob.setCronExpression(QuartzUtil.getCronBySeconds(Long.valueOf(SysConfigUtil.get(sysConfigType.getName()))));
            scheduleJobService.saveOrUpdate(scheduleJob);
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
    public String operationName() {
        return "注册响应";
    }

    @Override
    public String className() {
        return this.getClass().getCanonicalName();
    }
}
