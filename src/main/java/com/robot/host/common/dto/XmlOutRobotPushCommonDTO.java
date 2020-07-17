package com.robot.host.common.dto;

import lombok.Data;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * @description:通用的xml对应的dto
 *  在机器人状态发生变化时，机器人主机主动向巡视主机上报状态数据并接收响应消息。  状态  Type：值为 1
 * 机器人主机按照一定时间间隔定时、主动向巡视主机上报运行数据并接收响  运行信息  Type：值为 2
 * 机器人主机按照一定时间间隔定时、主动向巡视主机上报数据并接收响应消息。  气象信息 Type：值为 21
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
public class XmlOutRobotPushCommonDTO {
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
            "type",
            "value",
            "valueUnit",
            "unit",
    })
    public static class Item {
        @XmlAttribute(name = "robot_name")
        public String robotName;
        @XmlAttribute(name = "robot_code")
        public String robotCode;

        @XmlAttribute(name = "time")
        public String time;
        @XmlAttribute(name = "type")
        public String type;
        @XmlAttribute(name = "value")
        public String value;
        @XmlAttribute(name = "value_unit")
        public String valueUnit;
        @XmlAttribute(name = "unit")
        public String unit;
    }
    public XmlOutRobotPushCommonDTO() {
    }
}
