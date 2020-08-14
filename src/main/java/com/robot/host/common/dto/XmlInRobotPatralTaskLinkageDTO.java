package com.robot.host.common.dto;

/**
 * @description: 巡检下发任务立即执行DTO
 */

import lombok.Data;

import javax.xml.bind.annotation.*;
import java.util.List;


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
public class XmlInRobotPatralTaskLinkageDTO {

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
    public List<XmlInRobotPatralTaskLinkageDTO.Item> items;


    /**

     */

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "taskCode",
            "taskName",
            "priority",
            "deviceLevel",
            "deviceList",
    })
    @Data
    public static class Item {
        @XmlAttribute(name = "task_code")
        public String taskCode;
        @XmlAttribute(name = "task_name")
        public String taskName;
        @XmlAttribute(name = "priority")
        public String priority;
        @XmlAttribute(name = "device_level")
        public Integer deviceLevel;
        @XmlAttribute(name = "device_list")
        public String deviceList;


    }


}
