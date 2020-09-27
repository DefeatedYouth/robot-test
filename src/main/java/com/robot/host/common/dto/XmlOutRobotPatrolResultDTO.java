package com.robot.host.common.dto;

import lombok.Data;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * @program: com.face.ele.smart.modules.netty.xml
 * @description:机器人生成巡视结果时，主动向巡视主机上报结果并接收响应消息。
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
public class XmlOutRobotPatrolResultDTO {
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
            "robotCode",
            "taskName",
            "taskCode",
            "deviceName",
            "deviceId",
            "value",
            "valueUnit",
            "unit",
            "time",
            "recognitionType",
            "fileType",
            "filePath",
            "rectangle",
            "taskPatrolledId",
    })

    public static class Item {
        @XmlAttribute(name = "robot_code")
        public String robotCode;
        @XmlAttribute(name = "task_name")
        public String taskName;

        @XmlAttribute(name = "task_code")
        public String taskCode;
        @XmlAttribute(name = "device_name")
        public String deviceName;
        @XmlAttribute(name = "device_id")
        public String deviceId;
        @XmlAttribute(name = "value")
        public String value;
        @XmlAttribute(name = "value_unit")
        public String valueUnit;
        @XmlAttribute(name = "unit")
        public String unit;
        @XmlAttribute(name = "time")
        public String time;
        @XmlAttribute(name = "recognition_type")
        public String recognitionType;
        @XmlAttribute(name = "file_type")
        public String fileType;
        @XmlAttribute(name = "file_path")
        public String filePath;
        @XmlAttribute(name = "rectangle")
        public String rectangle;
        @XmlAttribute(name = "task_patrolled_id")
        public String taskPatrolledId;

        @XmlAttribute(name = "valid")
        public String valid;

    }

    public XmlOutRobotPatrolResultDTO() {
    }
}
