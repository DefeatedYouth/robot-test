package com.robot.host.common.constants;

import java.util.HashMap;
import java.util.Map;

public enum EnumPatrolExecStatus {
    //    @ApiModelProperty(value = "执行状态（0未开始，1周期中，2进行中，3已完成,4终止，5暂停）", dataType = "Integer")
    notstart(0, "未开始"),
    underway(1, "进行中"),
    finish(3, "已完成"),
    termination(4, "终止"),
    pause(5, "暂停"),
;


    private Integer value;
    private String text;

    private static Map<Integer, EnumPatrolExecStatus> pool = new HashMap<Integer, EnumPatrolExecStatus>();

    static {
        for (EnumPatrolExecStatus each : EnumPatrolExecStatus.values()) {
            EnumPatrolExecStatus defined = pool.get(each.getValue());
            if (null != defined) {
                pool.put(null, null);
            }
            pool.put(each.getValue(), each);
        }
    }


    EnumPatrolExecStatus(Integer code, String message) {
        this.value = code;
        this.text = message;
    }


    public static EnumPatrolExecStatus valueOf(Integer code) {
        return pool.get(code);
    }

    public Integer getValue() {
        return this.value;
    }

    public String getText() {
        return text;
    }
}

