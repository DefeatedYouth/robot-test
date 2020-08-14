package com.robot.host.common.constants;

import java.util.HashMap;
import java.util.Map;

public enum EnumOperationLogType {

    ROBOT_OPERATION(1,"机器人控制"),
    PATROL_TASK(2,"巡检任务")
    ;


    private Integer value;
    private String text;

    private static Map<Integer, EnumOperationLogType> pool = new HashMap<Integer, EnumOperationLogType>();

    static {
        for (EnumOperationLogType each : EnumOperationLogType.values()) {
            EnumOperationLogType defined = pool.get(each.getValue());
            if (null != defined) {
                pool.put(null, null);
            }
            pool.put(each.getValue(), each);
        }
    }


    EnumOperationLogType(Integer code, String message) {
        this.value = code;
        this.text = message;
    }


    public static EnumOperationLogType valueOf(Integer code) {
        return pool.get(code);
    }

    public Integer getValue() {
        return this.value;
    }

    public String getText() {
        return text;
    }
}
