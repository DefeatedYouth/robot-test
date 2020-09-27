package com.robot.host.common.dto;

import lombok.Data;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "modelList"
})
@XmlRootElement(name = "device_model")
@Data
public class RobotSynModelXmlDTO {

    @XmlElement(name = "model")
    public List<Model> modelList;
    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "deviceId",
            "deviceName",
            "bayId",
            "bayName",
            "mainDeviceId",
            "mainDeviceName",
    })
    public static class Model {
     /*   device_id="01d34ae2734b43249c1a1a2d208ca06a"                         //监控场景Id
        device_name="监控场景名称"                                          //监控场景名称：监控点名称
        device_type_item_name="设备类型实例名称"                            //设备名称
        bay_name="区域（电压等级）,间隔"                                   //场地中文路径：区域（电压等级）,间隔
        device_type="设备类型id"                                            //设备类型id
        meter_type="表计类型id"                                             //表计类型id
        appearance_type="外观类型id"                                        //外观类型id
        save_type_list="采集文件类型id组"                                   //采集文件类型id组：3,4
        recognition_type_list="识别类型"                                    //识别类型
        phase="A相"                                                          //A相，B相，C相
                />*/

        @XmlAttribute(name = "device_id")
        public String deviceId;//监控场景Id
        @XmlAttribute(name = "device_name")
        public String deviceName;//监控场景名称：监控点名称
        @XmlAttribute(name = "component_id")
        public String componentId;
        @XmlAttribute(name = "component_name")
        public String componentName;
        @XmlAttribute(name = "bay_id")
        public String bayId;
        @XmlAttribute(name = "bay_name")
        public String bayName;
        @XmlAttribute(name = "main_device_id")
        public String mainDeviceId;
        @XmlAttribute(name = "main_device_name")
        public String mainDeviceName;
        @XmlAttribute(name = "device_type")
        public String deviceType;
        @XmlAttribute(name = "meter_type")
        public String meterType;
        @XmlAttribute(name = "appearance_type")
        public String appearanceType;
        @XmlAttribute(name = "save_type_list")
        public String saveTypeList;
        @XmlAttribute(name = "recognition_type_list")
        public String recognitionTypeList;
        @XmlAttribute(name = "phase")
        public String phase;
        @XmlAttribute(name = "device_info")
        public String deviceInfo;
        @XmlAttribute(name = "device_type_item_name")
        public String deviceTypeItemName;

    }
}
