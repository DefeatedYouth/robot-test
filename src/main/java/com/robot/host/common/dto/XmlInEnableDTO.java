package com.robot.host.common.dto;

import lombok.Data;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * @author xiatian
 * @date 2020/9/22 9:24
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
public class XmlInEnableDTO {

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
    public List<XmlInEnableDTO.Item> items;

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "enable",
            "startTime",
            "endTime",
            "deviceLevel",
            "deviceList"
    })
    public static class Item {
        @XmlAttribute(name = "enable")
        public Integer enable;
        @XmlAttribute(name = "start_time")
        public String startTime;
        @XmlAttribute(name = "end_time")
        public String endTime;
        @XmlAttribute(name = "device_level")
        public Integer deviceLevel;
        @XmlAttribute(name = "device_list")
        public String deviceList;

    }
}
