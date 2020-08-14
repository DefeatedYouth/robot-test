package com.robot.host.common.dto;

import lombok.Data;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * @description 巡视主机主动向机器人主机下发联动任务，机器人收到请求后，立刻响应请求执行此任
 * 务或回复失败附带失败原因。
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
public class XmlOutRobotTaskLinkageDTO {
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
            "errorCode",
    })
    public static class Item {
        @XmlAttribute(name = "error_code")
        public String errorCode;

    }

    public XmlOutRobotTaskLinkageDTO() {
    }
}
