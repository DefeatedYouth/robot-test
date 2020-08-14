package com.robot.host.common.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: com.face.ele.smart.modules.netty.utils
 * @description: 在机器人状态发生变化时，机器人主机主动向巡视主机上报状态数据并接收响应消息
 * @author: dadi
 * @create: 2020-05-07 16:17
 */
public enum EnumRobotStatusDataType {
    /**
     <1>: = 电池电量低 <0>: = 正常
                          <1>: = 低
     <2>: = 通信状态异常 <0>: = 正常
                      <1>: = 异常
     <3>: = 超声停障 <0>: = 正常
                    <1>: = 停障
     <4>: = 驱动异常 <0>: = 正常
                  <1>: = 异常
     <21>: = 故障报警 <0>: = 正常
                      <1>: = 报警
     <41>: = 运行状态 <1>: = 空闲状态
                     <2>: = 巡视状态
                     <3>: = 充电状态
                     <4>: = 检修状态
     <61>: = 控制模式 <1>: = 任务模式
                 <2>: = 紧急定位
                 模式
                 <3>: = 后台遥控
                 模式
                 <4>: = 手持遥控
                 模式
     <81>: = 控制权状态 <0>: = 空闲
                     <1>: = 获得
     <101>: = 轮转状态 <0>: = 空闲
                     <1>: = 值班
     */
    lowPowerFlag(1,  "电池电量低"),
    communicationStatusFlag(2,  "通信状态异常"),
    ultrasoundFlag( 3, "超声停障"),
    driverUnusualFlag( 4,  "驱动异常"),
    currentStatus( 41,  "运行状态1、空闲状态;2、巡视状态;3、充电状态  4、检修状态"),
    taskModel( 61,  "任务模式1:任务模式，2紧急定位模式，3：后台遥控，4手持遥控"),
    controlPermissionStatus( 81,  "控制权状态 <0>: = 空闲  <1>: = 获得"),

    ;
    private Integer type;//类型
    private String typeName;

    private static Map<Integer, EnumRobotStatusDataType> pool = new HashMap<Integer, EnumRobotStatusDataType>();

    static {
        for (EnumRobotStatusDataType each : EnumRobotStatusDataType.values()) {
            EnumRobotStatusDataType defined = pool.get(each.getType());
            if (null != defined) {
                pool.put(null, null);
            }
            pool.put(each.type, each);
        }
    }


    EnumRobotStatusDataType(Integer type, String typeName) {
        this.type = type;
        this.typeName = typeName;
    }

    public static EnumRobotStatusDataType getEnum(Integer type) {
        return pool.get(type);
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }


    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

}
