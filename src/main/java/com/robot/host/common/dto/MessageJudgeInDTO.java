package com.robot.host.common.dto;

import lombok.Data;

/**
 * @program: com.face.ele.smart.modules.netty.dto
 * @description: 封装判断消息的类型  ,从机器人传到        巡视主机
 * @author: dadi
 * @create: 2020-05-15 13:31
 */
@Data
public class MessageJudgeInDTO {
    //大类
    String type;
    //子类
    String command;
}
