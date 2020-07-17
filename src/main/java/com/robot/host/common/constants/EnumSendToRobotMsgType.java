package com.robot.host.common.constants;

import java.util.HashMap;
import java.util.Map;

public enum EnumSendToRobotMsgType {
    // 监视消息类型
    STATUS_DATA(1, "状态数据"),
    RUN_DATA(2, "运行数据"),
    COORDINATE_DATA(3, "坐标"),
    PATROL_ROUTE(4,"巡视路线"),
    ABNORMAL_WARN(5,"异常告警数据"),
    WEATHER_DATA(21,"气象数据"),
    PATROL_TASK_STATUS(41,"任务状态数据"),
    PATROL_TASK_RESULT(61,"巡视结果")
    ;

    private Integer value;
    private String text;

    private static Map<Integer, EnumSendToRobotMsgType> pool = new HashMap<Integer, EnumSendToRobotMsgType>();

    static {
        for (EnumSendToRobotMsgType each : EnumSendToRobotMsgType.values()) {
            EnumSendToRobotMsgType defined = pool.get(each.getValue());
            if (null != defined) {
                pool.put(null, null);
            }
            pool.put(each.getValue(), each);
        }
    }

    EnumSendToRobotMsgType(Integer code, String message) {
        this.value = code;
        this.text = message;
    }


    public static EnumSendToRobotMsgType valueOf(Integer code) {
        return pool.get(code);
    }

    public Integer getValue() {
        return this.value;
    }

    public String getText() {
        return text;
    }


}
