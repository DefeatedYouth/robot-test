package com.robot.host.common.constants;

import java.util.HashMap;
import java.util.Map;

public enum EnumPatrolAuditStatus {
    //巡检任务审核状态
    wait(0, "待审核"),

    pass(1, "通过"),

    failed(-1, "不通过");


    private Integer value;
    private String text;

    private static Map<Integer, EnumPatrolAuditStatus> pool = new HashMap<Integer, EnumPatrolAuditStatus>();

    static {
        for (EnumPatrolAuditStatus each : EnumPatrolAuditStatus.values()) {
            EnumPatrolAuditStatus defined = pool.get(each.getValue());
            if (null != defined) {
                pool.put(null, null);
            }
            pool.put(each.getValue(), each);
        }
    }


    EnumPatrolAuditStatus(Integer code, String message) {
        this.value = code;
        this.text = message;
    }


    public static EnumPatrolAuditStatus valueOf(Integer code) {
        return pool.get(code);
    }

    public Integer getValue() {
        return this.value;
    }

    public String getText() {
        return text;
    }
}

