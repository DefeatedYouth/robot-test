package com.robot.host.common.constants;

import lombok.Data;

/**
 * @program: com.face.ele.smart.modules.netty.service
 * @description: 和机器人通信的协议消息对象
 * @author: dadi
 * @create: 2020-05-04 10:56
 */
@Data
public class ProtocolMessage {
    //EB90
    private int magicNumber ;
    private byte mainVersion;
    private byte modifyVersion;
    private long sessionId;
    private int length;

    private MessageTypeEnum messageType;
    private String body;

    //消息体解析的大类，消息体解析的子类



}
