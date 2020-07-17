package com.robot.host.common.dto;

import lombok.Data;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * @program: com.face.ele.smart.modules.netty.xml
 * @description巡视主机向机器人主机发起巡视任务控制请求，机器人主机收到请求后开展巡视作业并
发送响应消息  接收响应
 * @author: dadi
 * @create: 2020-05-07 11:41
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "sendCode",
        "receiveCode",
        "type",
        "code",
        "command",
        "time",
        "items"
})
@XmlRootElement(name = "Robot")
@Data
public class XmlInRobotTaskControlDTO {
    @XmlElement(name = "SendCode")
    public String sendCode;
    @XmlElement(name = "ReceiveCode")
    public String receiveCode;
    @XmlElement(name = "Type")
    public String type;
    @XmlElement(name = "Code")
    public String code;
    @XmlElement(name = "Command")
    public String command;
    @XmlElement(name = "Time")
    public String time;
    @XmlElement(name = "Item")
    @XmlElementWrapper(name = "Items")
    public List<Item> items;

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "taskPatrolledId",
    })
    public static class Item {
        @XmlAttribute(name = "task_patrolled_id")
          public String taskPatrolledId;

    }

    public XmlInRobotTaskControlDTO() {
    }
}
