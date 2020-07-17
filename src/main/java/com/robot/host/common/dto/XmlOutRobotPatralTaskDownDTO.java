package com.robot.host.common.dto;

/**
 * @program: com.face.ele.smart.modules.netty.dto
 * @description: 巡检下发任务DTO
 * @author: dadi
 * @create: 2020-05-13 14:42
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
public class XmlOutRobotPatralTaskDownDTO {
//public class XmlOutRobotPatralTaskDownDTO extends BaseXmlDTO {

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


    /**
     * type string 巡视类型 <1>: = 全面巡视
     * <2>: = 例行巡视
     * <3>: = 专项巡视
     * <4>: = 特殊巡视
     * task_code string 任务编码
     * task_name string 任务名称
     * priority string 优先级 <1>: = 优先级1，优先级最低
     * <2>: = 优先级2
     * <3>: = 优先级3
     * <4>: = 优先级4，优先级最高
     * device_level int 设备层级 <1>: = 间隔
     * <2>: = 主设备
     * <3>: = 设备点位
     * device_list string 设备列表 格式：多个ID，采用“,”分隔
     * fixed_start_time string 定期开始时间
     * <p>
     * <p>
     * cycle_month string 周期（月） 格式：多个月，采用“,”分隔
     * cycle_week string 周期（周） 格式：多个周，采用“,”分隔
     * cycle_execute_time string 周期（执行时间） 格式：HH:mm:ss
     * cycle_start_time string 周期开始时间
     * cycle_end_time string 周期结束时间
     * interval_number string 间隔（数量）
     * interval_type string 间隔（类型） <1>: = 小时
     * <2>: = 天
     * interval_execute_time string 间隔（执行时间） 格式：HH:mm:ss
     * interval_start_time string 间隔开始时间
     * interval_end_time string 间隔结束时间
     * invalid_start_time string 不可用开始时间
     * invalid_end_time string 不可用结束时间
     * isenable string 是否可用 <0>: = 可用
     * <1>: = 不可用
     * creator string 编制人
     * create_time string 编制时
     */

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "type",
            "taskCode",
            "taskName",
            "priority",
            "deviceLevel",
            "deviceList",
            "fixedStartTime",
            "cycleMonth",
            "cycleWeek",
            "cycleExecuteTime",
            "cycleStartTime",
            "cycleEndTime",
            "intervalNumber",
            "intervalType",
            "intervalExecuteTime",
            "intervalStartTime",
            "intervalEndTime",
            "isenable",
            "creator",
            "createTime",
    })
    @Data
    public static class Item {
        @XmlAttribute(name = "type")
        public String type;
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
        @XmlAttribute(name = "fixed_start_time")
        public String fixedStartTime;
        @XmlAttribute(name = "cycle_month")
        public String cycleMonth;

        @XmlAttribute(name = "cycle_week")
        public String cycleWeek;
        @XmlAttribute(name = "cycle_execute_time")
        public String cycleExecuteTime;
        @XmlAttribute(name = "cycle_start_time")
        public String cycleStartTime;
        @XmlAttribute(name = "cycle_end_time")
        public String cycleEndTime;
        @XmlAttribute(name = "interval_number")
        public String intervalNumber;

        @XmlAttribute(name = "interval_type")
        public String intervalType;

        @XmlAttribute(name = "interval_execute_time")
        public String intervalExecuteTime;
        @XmlAttribute(name = "interval_start_time")
        public String intervalStartTime;
        @XmlAttribute(name = "interval_end_time")
        public String intervalEndTime;
        @XmlAttribute(name = "invalid_start_time")
        public String invalidStartTime;
        @XmlAttribute(name = "invalid_end_time")
        public String invalidEndTime;
        @XmlAttribute(name = "isenable")
        public String isenable;
        @XmlAttribute(name = "creator")
        public String creator;
        @XmlAttribute(name = "create_time")
        public String createTime;

    }


}
