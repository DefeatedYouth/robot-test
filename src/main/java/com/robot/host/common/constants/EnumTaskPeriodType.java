package com.robot.host.common.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: com.face.ele.smart.modules.smart.constants
 * @description: 任务执行类型
 * @author: dadi
 * @create: 2020-05-20 11:17
 */
public enum EnumTaskPeriodType {
    HOUR(1, "小时"),

    DAY(2, "天"),

    WEEK(3, "周"),

    MONTH(4, "月"),
    TIMEDTASK(5, "定时任务");

    private Integer value;
    private String text;

    private static Map<Integer, EnumTaskPeriodType> pool = new HashMap<Integer, EnumTaskPeriodType>();

    static {
        for (EnumTaskPeriodType each : EnumTaskPeriodType.values()) {
            EnumTaskPeriodType defined = pool.get(each.getValue());
            if (null != defined) {
                pool.put(null, null);
            }
            pool.put(each.getValue(), each);
        }
    }


    EnumTaskPeriodType(Integer code, String message) {
        this.value = code;
        this.text = message;
    }


    public static EnumTaskPeriodType valueOf(Integer code) {
        return pool.get(code);
    }

    public Integer getValue() {
        return this.value;
    }

    public String getText() {
        return text;
    }
}

