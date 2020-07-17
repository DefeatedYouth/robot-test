package com.robot.host.common.dto;

import lombok.Data;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * @program: com.face.ele.smart.modules.netty.xml
 * @author: dadi  模型同步机器人数据
 * @create: 2020-05-07 11:41
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "modelList"
})
@XmlRootElement(name = "robot_model")
@Data
public class RobotSynRobotModelXmlDTO {

    @XmlElement(name = "model")
    public List<Model> modelList;

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "robotName",
            "robotCode",
            "manufacturer",
            "istransport",
            "type",
            "mappath",
            "robotInfo",
    })
    public static class Model {

        @XmlAttribute(name = "robot_name")
        public String robotName;//机器人名称
        @XmlAttribute(name = "robot_code")
        public String robotCode;//机器人编码
        @XmlAttribute(name = "manufacturer")
        public String manufacturer;
        @XmlAttribute(name = "istransport")
        public String istransport;
        @XmlAttribute(name = "type")
        public String type;
        @XmlAttribute(name = "mappath")
        public String mappath;
        @XmlAttribute(name = "robot_info")
        public String robotInfo;


    }
}
