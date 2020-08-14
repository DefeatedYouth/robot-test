package com.robot.host.common.constants;

import java.util.HashMap;
import java.util.Map;

public enum EnumTopicDistination {
    SYS_LOG(1,"/sysLog"),
    PATROL_TASK(2,"/task"),
    ROBOT_OPERATION(3,"/robot")
    ;



    private Integer value;
    private String text;

    private static Map<Integer, EnumTopicDistination> pool = new HashMap<Integer, EnumTopicDistination>();

    static {
        for (EnumTopicDistination each : EnumTopicDistination.values()) {
            EnumTopicDistination defined = pool.get(each.getValue());
            if (null != defined) {
                pool.put(null, null);
            }
            pool.put(each.getValue(), each);
        }
    }


    EnumTopicDistination(Integer code, String message) {
        this.value = code;
        this.text = message;
    }


    public static EnumTopicDistination valueOf(Integer code) {
        return pool.get(code);
    }

    public Integer getValue() {
        return this.value;
    }

    public String getText() {
        return text;
    }
}
