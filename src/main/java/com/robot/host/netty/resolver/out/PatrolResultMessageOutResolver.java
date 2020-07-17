package com.robot.host.netty.resolver.out;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.robot.host.common.constants.EnumSendToRobotMsgType;
import com.robot.host.common.constants.NettyConstants;
import com.robot.host.common.constants.ProtocolMessage;
import com.robot.host.common.dto.MessageAboutRobotDTO;
import com.robot.host.common.dto.PatrolTaskResultDTO;
import com.robot.host.common.dto.XmlOutRobotPatrolResultDTO;
import com.robot.host.base.entry.PatrolTaskResultEntity;
import com.robot.host.base.service.PatrolTaskResultService;
import com.robot.host.common.util.XmlBeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.testng.collections.Lists;

import java.util.List;

@Slf4j
public class PatrolResultMessageOutResolver extends CommonOutResolver {


    private PatrolTaskResultService patrolTaskResultService;

    public PatrolResultMessageOutResolver(PatrolTaskResultService patrolTaskResultService) {
        this.patrolTaskResultService = patrolTaskResultService;
    }

    @Override
    public boolean support(MessageAboutRobotDTO outDTO) {
        EnumSendToRobotMsgType robotMsgType = outDTO.getMsgType();
        if(robotMsgType == EnumSendToRobotMsgType.PATROL_TASK_RESULT){
            return true;
        }
        return false;
    }

    @Override
    protected ProtocolMessage concreteResolve(MessageAboutRobotDTO busiMessage) {

        PatrolTaskResultDTO patrolTaskResult = JSONUtil.toBean(busiMessage.getMsgBody(), PatrolTaskResultDTO.class);
        List<PatrolTaskResultEntity> patrolTaskResultList = patrolTaskResultService.list(new QueryWrapper<PatrolTaskResultEntity>().lambda().eq(PatrolTaskResultEntity::getTaskCode, patrolTaskResult.getTaskCode()));

        //封装XML
        XmlOutRobotPatrolResultDTO patrolResultVO = new XmlOutRobotPatrolResultDTO();
        patrolResultVO.setSendCode(NettyConstants.ROBOT_HOST_CODE);
        patrolResultVO.setReceiveCode(NettyConstants.PATROL_HOST_CODE);
        patrolResultVO.setType(NettyConstants.IN_CODE_TASK_RESULT + "");
        List<XmlOutRobotPatrolResultDTO.Item> items = Lists.newArrayList();
        patrolTaskResultList.forEach(result -> {
            XmlOutRobotPatrolResultDTO.Item item = new XmlOutRobotPatrolResultDTO.Item();
            item.setRobotCode(result.getRobotCode());
            item.setTaskName(result.getTaskName());
            item.setTaskCode(result.getTaskCode());
            item.setDeviceName(result.getDeviceName());
            item.setDeviceId(result.getDeviceId() + "");
            item.setValue(result.getValue());
            item.setValueUnit(result.getValue() + result.getUnit());
            item.setUnit(result.getUnit());
            item.setTime(result.getTime().toString());
            item.setRecognitionType(result.getRecognitionType() + "");
            item.setFileType(result.getFileType() + "");
            item.setRectangle(result.getRectangle());
            item.setTaskPatrolledId(result.getTaskPatrolledId() + "");
            items.add(item);
        });
        patrolResultVO.setItems(items);

        ProtocolMessage protocol = new ProtocolMessage();
        String patrolTaskResultMsg = XmlBeanUtils.beanToXml(patrolResultVO, XmlOutRobotPatrolResultDTO.class);
        protocol.setBody(patrolTaskResultMsg);
        return protocol;
    }
}
