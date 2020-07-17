package com.robot.host.common.dto;

import com.robot.host.common.constants.EnumRobotOperationType;
import com.robot.host.common.constants.EnumSendToRobotMsgType;
import lombok.Data;

/**
 * @program: com.face.ele.smart.modules.netty.dto
 * @description:关于机器人操作的消息队列用到的dto
 * @author: dadi
 * @create: 2020-05-11 10:38
 */
@Data
public class MessageAboutRobotDTO {
    private EnumSendToRobotMsgType msgType;
    private EnumRobotOperationType subMsgType;
    private  String robotCode;
    private  String siteCode;
    private  String msgBody;

}
