package com.robot.host.common.constants;

import java.util.HashMap;
import java.util.Map;

public enum EnumPatrolRuleType {
//巡检任务类型(（1实时任务、2定时任务、3周期任务）)
  actualTask(1,"实时任务"),

    periodTask(2,"定时任务"),

    weeklyTask(3,"周期任务");


    private Integer value;
    private String text;

    private static Map<Integer, EnumPatrolRuleType> pool = new HashMap<Integer, EnumPatrolRuleType>();

    static {
        for (EnumPatrolRuleType each : EnumPatrolRuleType.values()) {
            EnumPatrolRuleType defined = pool.get(each.getValue());
            if (null != defined) {
                pool.put(null, null);
            }
            pool.put(each.getValue(), each);
        }
    }


    EnumPatrolRuleType(Integer code, String message) {
        this.value = code;
        this.text = message;
    }


    public static EnumPatrolRuleType valueOf(Integer code) {
        return pool.get(code);
    }

    public Integer getValue() {
        return this.value;
    }

    public String getText() {
        return text;
    }
}

