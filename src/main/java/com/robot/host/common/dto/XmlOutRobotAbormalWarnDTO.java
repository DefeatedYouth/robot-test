package com.robot.host.common.dto;

import lombok.Data;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * @program: com.face.ele.smart.modules.netty.xml
 * @description异常告警
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
public class XmlOutRobotAbormalWarnDTO {
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
            "robotName",
            "robotCode",
            "time",
            "content",
    })
    public static class Item {
        @XmlAttribute(name = "robot_name")
          public String robotName;
        @XmlAttribute(name = "robot_code")
        public String robotCode;
        @XmlAttribute(name = "time")
        public String time;
        @XmlAttribute(name = "content")
        public String content;

    }

    public XmlOutRobotAbormalWarnDTO() {
    }
}
