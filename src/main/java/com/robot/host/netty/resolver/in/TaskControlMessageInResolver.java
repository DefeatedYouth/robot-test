package com.robot.host.netty.resolver.in;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.robot.host.base.service.OperationLogService;
import com.robot.host.common.constants.*;
import com.robot.host.common.dto.MessageAboutRobotDTO;
import com.robot.host.common.dto.MessageJudgeInDTO;
import com.robot.host.common.dto.PatrolTaskStatusDTO;
import com.robot.host.common.dto.XmlInRobotTaskControlDTO;
import com.robot.host.base.entry.PatrolTaskEntity;
import com.robot.host.common.util.MessageUtil;
import com.robot.host.quartz.constants.JobConstants;
import com.robot.host.quartz.entry.ScheduleJobEntity;
import com.robot.host.quartz.service.ScheduleJobService;
import com.robot.host.base.service.PatrolTaskService;
import com.robot.host.common.util.SessionSocketHolder;
import com.robot.host.common.util.XmlBeanUtils;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class TaskControlMessageInResolver implements InResolver {

    private ScheduleJobService scheduleJobService;

    private PatrolTaskService patrolTaskService;

    private OperationLogService operationLogService;

    public TaskControlMessageInResolver(ScheduleJobService scheduleJobService, PatrolTaskService patrolTaskService, OperationLogService operationLogService) {
        this.scheduleJobService = scheduleJobService;
        this.patrolTaskService = patrolTaskService;
        this.operationLogService = operationLogService;
    }

    @Override
    public boolean support(MessageJudgeInDTO judgeInDTO) {
        EnumRobotTaskControlType taskControl = EnumRobotTaskControlType.getEnum(judgeInDTO.getType() + "-" + judgeInDTO.getCommand());
        if (taskControl == null) {
            return false;
        }
        return true;
    }

    @Override
    public ProtocolMessage resolve(ProtocolMessage message, ChannelHandlerContext ctx) {
        Boolean flag = false;
        XmlInRobotTaskControlDTO taskControl = XmlBeanUtils.xmlToBean(message.getBody(), XmlInRobotTaskControlDTO.class);
        // 获取 ScheduleJob
        PatrolTaskEntity patrolTask = patrolTaskService.getOne(new QueryWrapper<PatrolTaskEntity>().lambda().eq(PatrolTaskEntity::getPatrolTaskCode, taskControl.getCode()));
        String paramsLike = "\"" + JobConstants.QUARTZ_BUSI_ID + "\":" + patrolTask.getPatrolTaskId();
        QueryWrapper<ScheduleJobEntity> jobQueryWrapper = new QueryWrapper<>();
        jobQueryWrapper.lambda().like(ScheduleJobEntity::getParams,paramsLike);
        List<ScheduleJobEntity> scheduleJobList = scheduleJobService.list(jobQueryWrapper);
        //周期任务  停一个  停所有
        for (ScheduleJobEntity scheduleJobEntity : scheduleJobList) {
            Long jobId = scheduleJobEntity.getJobId();
            if (EnumRobotTaskControlType.START.getCommand() == Integer.valueOf(taskControl.getCommand())){
                //启动任务
            }else if (EnumRobotTaskControlType.PAUSE.getCommand() == Integer.valueOf(taskControl.getCommand())){
                //任务暂停
                scheduleJobService.pause(new Long[]{jobId});
            }else if (EnumRobotTaskControlType.CONTINUE.getCommand() == Integer.valueOf(taskControl.getCommand())){
                //任务继续
                scheduleJobService.resume(new Long[]{jobId});
            }else if (EnumRobotTaskControlType.STOP.getCommand() == Integer.valueOf(taskControl.getCommand())){
                //任务停止
                scheduleJobService.deleteBatch(new Long[]{jobId});
                flag = true;
            }
            operationLogService.saveSysLogThenSendWebSocket(SysLogConstant.ROBOT_OTHER,
                    SysLogConstant.SYS_LOCAL_STATUS,
                    String.format("[%s]正在修改任务状态，jobid：%s，任务状态：%s", operationName(), jobId, EnumRobotTaskControlType.getEnum(taskControl.getCode() + "-" + taskControl.getCommand())),
                    null,null,null,className());
        }

        //删除定时任务（子任务）时同时删除巡检任务（主任务）
        if(flag){
            patrolTaskService.removeById(patrolTask.getPatrolTaskId());
        }

        //返回任务状态
        this.sendTaskStatus(patrolTask);


        //返回响应信息
        ProtocolMessage respMsg = new ProtocolMessage();
        XmlInRobotTaskControlDTO respDTO = returnMessage(NettyConstants.RESPONSE_CODE_SUCCESS + "", patrolTask.getPatrolTaskId() + "");
        String jsonMsg = XmlBeanUtils.beanToXml(respDTO, XmlInRobotTaskControlDTO.class);
        respMsg.setBody(jsonMsg);
        respMsg.setSessionId(message.getSessionId());

        NioSocketChannel channel = SessionSocketHolder.get(NettyConstants.ROBOT_HOST_CODE);
        ChannelFuture future = channel.writeAndFlush(respMsg);
        future.addListener((ChannelFutureListener) channelFuture ->
                log.info("[任务控制]响应信息：{}",jsonMsg));
        return respMsg;
    }

    private void sendTaskStatus(PatrolTaskEntity patrolTask) {
        //推送任务状态
        PatrolTaskStatusDTO taskStatusDTO = new PatrolTaskStatusDTO();
        taskStatusDTO.setTaskCode(patrolTask.getPatrolTaskCode());
        String taskStatusMsg = JSONUtil.toJsonStr(taskStatusDTO);

        MessageAboutRobotDTO messageAboutRobotDTO = new MessageAboutRobotDTO();
        messageAboutRobotDTO.setMsgType(EnumSendToRobotMsgType.PATROL_TASK_STATUS);
        messageAboutRobotDTO.setMsgBody(taskStatusMsg);
        String jsonStr = JSONUtil.toJsonStr(messageAboutRobotDTO);
        MessageUtil.sendMessage(jsonStr);
    }

    @Override
    public String operationName() {
        return "任务控制";
    }

    @Override
    public String className() {
        return this.getClass().getCanonicalName();
    }


    /**
     * 响应信息
     * @param code
     * @param taskPartrolledId
     * @return
     */
    private XmlInRobotTaskControlDTO returnMessage(String code, String taskPartrolledId){
        XmlInRobotTaskControlDTO robotTaskControl = new XmlInRobotTaskControlDTO();
        robotTaskControl.setSendCode(NettyConstants.ROBOT_HOST_CODE);
        robotTaskControl.setReceiveCode(NettyConstants.PATROL_HOST_CODE);
        robotTaskControl.setType(NettyConstants.IN_OUT_CODE_SYSTEM_TYPE + "");
        robotTaskControl.setCommand(NettyConstants.OUT_CODE_SYSTEM_TYPE_COMMAND_COMMON_ITEMS + "");
        robotTaskControl.setCode(code);
        XmlInRobotTaskControlDTO.Item item = new XmlInRobotTaskControlDTO.Item();
        item.setTaskPatrolledId(taskPartrolledId);
        robotTaskControl.setItems(Arrays.asList(item));
        return robotTaskControl;
    }
}
