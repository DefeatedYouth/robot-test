package com.robot.host.netty.resolver.in;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.robot.host.base.entry.DeviceInfoEntry;
import com.robot.host.base.entry.RobotInfoEntity;
import com.robot.host.base.service.DeviceInfoService;
import com.robot.host.base.service.OperationLogService;
import com.robot.host.base.service.RobotInfoService;
import com.robot.host.common.constants.NettyConstants;
import com.robot.host.common.constants.ProtocolMessage;
import com.robot.host.common.constants.SysLogConstant;
import com.robot.host.common.dto.MessageJudgeInDTO;
import com.robot.host.common.dto.XmlInRobotPatralTaskLinkageDTO;
import com.robot.host.common.dto.XmlOutRobotTaskLinkageDTO;
import com.robot.host.common.util.XmlBeanUtils;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang3.StringUtils;
import org.testng.collections.Lists;

import java.util.List;
import java.util.stream.Collectors;

public class TaskLinkageMessageInResolver implements  InResolver {


    private RobotInfoService robotInfoService;

    private OperationLogService operationLogService;

    private DeviceInfoService deviceInfoService;

    public TaskLinkageMessageInResolver(RobotInfoService robotInfoService, OperationLogService operationLogService, DeviceInfoService deviceInfoService) {
        this.robotInfoService = robotInfoService;
        this.operationLogService = operationLogService;
        this.deviceInfoService = deviceInfoService;
    }

    @Override
    public boolean support(MessageJudgeInDTO judgeInDTO) {
        if (!StringUtils.equals(judgeInDTO.getType(), NettyConstants.OUT_CODE_TASK_DOWN_INSTRUCT + "")) {
            return false;
        }
        if (!StringUtils.equals(judgeInDTO.getCommand(), NettyConstants.OUT_CODE_TASK_DOWN_INSTRUCT_COMMAND + "")) {
            return false;
        }
        return true;
    }

    @Override
    public ProtocolMessage resolve(ProtocolMessage message, ChannelHandlerContext ctx) {
        ProtocolMessage protocol = new ProtocolMessage();
        XmlInRobotPatralTaskLinkageDTO taskLink = XmlBeanUtils.xmlToBean(message.getBody(), XmlInRobotPatralTaskLinkageDTO.class);
//        taskLink.getItems().get(0).get
        XmlOutRobotTaskLinkageDTO outTaskLink = this.executeTaskLink(taskLink);

        String respbody = XmlBeanUtils.beanToXml(outTaskLink, XmlOutRobotTaskLinkageDTO.class);
        protocol.setBody(respbody);
        return protocol;
    }

    //执行联动任务
    private XmlOutRobotTaskLinkageDTO executeTaskLink(XmlInRobotPatralTaskLinkageDTO taskLink) {
        XmlOutRobotTaskLinkageDTO result = new XmlOutRobotTaskLinkageDTO();
        result.setSendCode(NettyConstants.ROBOT_HOST_CODE);
        result.setReceiveCode(NettyConstants.PATROL_HOST_CODE);
        result.setType(NettyConstants.IN_OUT_CODE_SYSTEM_TYPE + "");
        result.setCommand(NettyConstants.OUT_CODE_SYSTEM_TYPE_COMMAND_COMMON_ITEMS + "");
        XmlOutRobotTaskLinkageDTO.Item item = new XmlOutRobotTaskLinkageDTO.Item();
        item.setTaskPatrolledId(taskLink.getItems().get(0).getTaskCode() + "_" + NettyConstants.fileDateFormat.format(System.currentTimeMillis()));
        List<RobotInfoEntity> robotList = robotInfoService.list(new QueryWrapper<RobotInfoEntity>().lambda().eq(RobotInfoEntity::getStatus, NettyConstants.ROBOT_FAULT));
        if(robotList.isEmpty()){//机器人故障
            result.setCode(NettyConstants.RESPONSE_CODE_ERROR + "");
            item.setErrorCode(NettyConstants.TASK_LINK_ROBOT_FAULT + "");
        }else{
            List<RobotInfoEntity> frees = robotList.stream().filter(robot -> robot.getStatus().equals(NettyConstants.ROBOT_FREE)).collect(Collectors.toList());
            if(!frees.isEmpty()){
                RobotInfoEntity robot = frees.get(0);
                this.taskLinkStart(robot,taskLink.getItems().get(0));
                result.setCode(NettyConstants.RESPONSE_CODE_SUCCESS + "");
                item.setErrorCode(NettyConstants.TASK_LINK_SUCCESS + "");
            }
        }
        List<XmlOutRobotTaskLinkageDTO.Item> items = Lists.newArrayList(item);
        result.setItems(items);
        return result;
    }

    private void taskLinkStart(RobotInfoEntity robot, XmlInRobotPatralTaskLinkageDTO.Item item) {
        operationLogService.saveSysLogThenSendWebSocket(SysLogConstant.ROBOT_PATROL_TASK,
                SysLogConstant.SYS_LOCAL_STATUS,
                String.format(SysLogConstant.ROBOT_TASK_START, operationName(), robot.getCode(), item.getTaskCode()),
                robot.getRobotInfoId(),
                null,null, className());
        List<DeviceInfoEntry> devices = deviceInfoService.list(new QueryWrapper<DeviceInfoEntry>().lambda().in(DeviceInfoEntry::getDeviceId,item.getDeviceList().split(",")));
        devices.forEach(device -> {
            operationLogService.saveSysLogThenSendWebSocket(SysLogConstant.ROBOT_PATROL_TASK,
                    SysLogConstant.SYS_LOCAL_STATUS,
                    String.format(SysLogConstant.ROBOT_TASK_ING, operationName(), robot.getCode(), device.getDeviceName()),
                    robot.getRobotInfoId(),
                    null,device.getDeviceId(), className());
            //模拟巡检
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        operationLogService.saveSysLogThenSendWebSocket(SysLogConstant.ROBOT_PATROL_TASK,
                SysLogConstant.SYS_LOCAL_STATUS,
                String.format(SysLogConstant.ROBOT_TASK_END, operationName(), robot.getCode(), item.getTaskCode()),
                robot.getRobotInfoId(),
                null,null, className());

    }


    @Override
    public String operationName() {
        return "联动任务";
    }

    @Override
    public String className() {
        return this.getClass().getCanonicalName();
    }
}
