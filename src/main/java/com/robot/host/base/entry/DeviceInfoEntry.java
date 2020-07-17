package com.robot.host.base.entry;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 *
 * 设备模型
 *
 */
@Data
@TableName("robot_device_info")
@ApiModel("设备模型")
public class DeviceInfoEntry implements Serializable {

    private static final long serialVersionUID = 1L;

    @ExcelProperty(value = "设备点位ID", index = 0)
    private String deviceId;

    @ExcelProperty(value = "设备点位名称", index = 1)
    private String deviceName;

    @ExcelProperty(value = "间隔ID", index = 2)
    private String bayId;

    @ExcelProperty(value = "间隔名称", index = 3)
    private String bayName;

    @ExcelProperty(value = "主设备ID", index = 4)
    private String mainDeviceId;

    @ExcelProperty(value = "主设备名称", index = 5)
    private String mainDeviceName;

    @ExcelProperty(value = "主设备类型", index = 6)
    private Integer deviceType;

    @ExcelProperty(value = "表计类型", index = 7)
    private Integer meterType;

    @ExcelProperty(value = "外观类型", index = 8)
    private Integer appearanceType;

    @ExcelProperty(value = "采集/保存文件类型列表", index = 9)
    private String saveTypeList;

    @ExcelProperty(value = "识别类型列表", index = 10)
    private String recognitionTypeList;

    @ExcelProperty(value = "相位", index = 11)
    private Integer phase;

    @ExcelProperty(value = "备注信息", index = 12)
    private String deviceInfo;

    private String posX;

    private String posY;

}
