package com.robot.host.netty.resolver.out;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.robot.host.common.constants.EnumSendToRobotMsgType;
import com.robot.host.common.constants.NettyConstants;
import com.robot.host.common.constants.ProtocolMessage;
import com.robot.host.common.dto.MessageAboutRobotDTO;
import com.robot.host.common.dto.PatrolTaskStatusDTO;
import com.robot.host.common.dto.XmlOutRobotPatralTaskStatusDTO;
import com.robot.host.base.entry.PatrolTaskEntity;
import com.robot.host.base.entry.PatrolTaskExecEntity;
import com.robot.host.base.service.PatrolTaskExecService;
import com.robot.host.base.service.PatrolTaskService;
import com.robot.host.common.util.RobotDateUtil;
import com.robot.host.common.util.XmlBeanUtils;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

/**
 * 任务状态数据
 */
@Slf4j
public class PatralTaskStatusOutResolver extends CommonOutResolver {

    private PatrolTaskService patrolTaskService;

    private PatrolTaskExecService patrolTaskExecService;

    public PatralTaskStatusOutResolver(PatrolTaskService patrolTaskService, PatrolTaskExecService patrolTaskExecService) {
        this.patrolTaskService = patrolTaskService;
        this.patrolTaskExecService = patrolTaskExecService;
    }

    @Override
    public boolean support(MessageAboutRobotDTO outDTO) {
        EnumSendToRobotMsgType robotMsgType = outDTO.getMsgType();
        if(robotMsgType == EnumSendToRobotMsgType.PATROL_TASK_STATUS){
            return true;
        }
        return false;
    }

    @Override
    protected ProtocolMessage concreteResolve(MessageAboutRobotDTO busiMessage) {
        ProtocolMessage protocol = new ProtocolMessage();


        PatrolTaskStatusDTO patrolTaskStatusDTO = JSONUtil.toBean(busiMessage.getMsgBody(), PatrolTaskStatusDTO.class);

        PatrolTaskEntity patrolTask = patrolTaskService.getOne(new QueryWrapper<PatrolTaskEntity>().lambda().eq(PatrolTaskEntity::getPatrolTaskCode, patrolTaskStatusDTO.getTaskCode()));
        PatrolTaskExecEntity patrolTaskExec = patrolTaskExecService.getOne(new QueryWrapper<PatrolTaskExecEntity>().lambda().eq(PatrolTaskExecEntity::getPatrolTaskCode, patrolTask.getPatrolTaskCode()));

        //封装XML
        XmlOutRobotPatralTaskStatusDTO patralTaskStatusVO = new XmlOutRobotPatralTaskStatusDTO();
        patralTaskStatusVO.setSendCode(NettyConstants.ROBOT_HOST_CODE);
        patralTaskStatusVO.setReceiveCode(NettyConstants.PATROL_HOST_CODE);
        patralTaskStatusVO.setType(NettyConstants.IN_CODE_TASK_STATUS + "");
        XmlOutRobotPatralTaskStatusDTO.Item item = new XmlOutRobotPatralTaskStatusDTO.Item();
        //TODO  巡视任务执行id
        item.setTaskPatrolledId(patrolTaskExec == null ? null : patrolTaskExec.getPatrolTaskExecId().toString());

        item.setTaskName(patrolTask.getPatrolTaskName());
        item.setTaskCode(patrolTask.getPatrolTaskCode());
        //TODO 任务状态
        item.setTaskState(patrolTask.getExecStatus() + "");

        item.setPlanStartTime(patrolTask.getTaskBeginTime().toString());
        item.setStartTime(patrolTask.getRealBeginTime().toString());
        //任务进度
        BigDecimal taskProgress = RobotDateUtil.countTaskProgress(patrolTask.getRealBeginTime(), patrolTask.getTaskEndTime());
        item.setTaskProgress(taskProgress == null ? null : taskProgress + "%");
        item.setDescription(patrolTaskStatusDTO.getDescription());
        String taskStatusMsg = XmlBeanUtils.beanToXml(patralTaskStatusVO, XmlOutRobotPatralTaskStatusDTO.class);

        protocol.setBody(taskStatusMsg);
        return protocol;
    }
}
