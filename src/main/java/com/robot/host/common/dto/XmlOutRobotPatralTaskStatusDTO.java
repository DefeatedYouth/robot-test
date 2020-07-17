package com.robot.host.common.dto;

import lombok.Data;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * @program: com.face.ele.smart.modules.netty.xml
 * @description:任务状态机器人同步过来
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
public class XmlOutRobotPatralTaskStatusDTO {
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
            "taskName",
            "taskCode",
            "taskState",
            "planStartTime",
            "startTime",
            "taskProgress",
            "taskEstimatedTime",
            "description",
    })
    public static class Item {

        @XmlAttribute(name = "task_patrolled_id")
        public String taskPatrolledId;
        @XmlAttribute(name = "task_name")
        public String taskName;

        @XmlAttribute(name = "task_code")
        public String taskCode;
        @XmlAttribute(name = "task_state")
        public String taskState;
        @XmlAttribute(name = "plan_start_time")
        public String planStartTime;
        @XmlAttribute(name = "start_time")
        public String startTime;
        @XmlAttribute(name = "task_progress")
        public String taskProgress;
        @XmlAttribute(name = "task_estimated_time")
        public String taskEstimatedTime;
        @XmlAttribute(name = "description")
        public String description;



    }

    public XmlOutRobotPatralTaskStatusDTO() {
    }
}
