package com.robot.host.common.constants;

import java.util.HashMap;
import java.util.Map;

public enum EnumPatrolType {
    //    "巡检类型(2例行巡检、1全面巡检、4特殊巡检、5熄灯巡检、3专项巡检、6自定义巡检)", dataType = "Integer")
    overall(1, "全面巡检"),
    routine(2, "例行巡检"),
    assign(3, "专项巡检"),
    special(4, "特殊巡检"),
    lightout(5, "熄灯巡检"),
    custom(6, "自定义巡检");


    private Integer value;
    private String text;

    private static Map<Integer, EnumPatrolType> pool = new HashMap<Integer, EnumPatrolType>();

    static {
        for (EnumPatrolType each : EnumPatrolType.values()) {
            EnumPatrolType defined = pool.get(each.getValue());
            if (null != defined) {
                pool.put(null, null);
            }
            pool.put(each.getValue(), each);
        }
    }


    EnumPatrolType(Integer code, String message) {
        this.value = code;
        this.text = message;
    }


    public static EnumPatrolType valueOf(Integer code) {
        return pool.get(code);
    }

    public Integer getValue() {
        return this.value;
    }

    public String getText() {
        return text;
    }
}

