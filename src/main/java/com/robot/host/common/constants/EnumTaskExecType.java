package com.robot.host.common.constants;

import com.alibaba.fastjson.annotation.JSONType;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.HashMap;
import java.util.Map;

@JSONType(serializeEnumAsJavaBean = true)
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum EnumTaskExecType {

    WAITING("WAITING", "等待"),
    PAUSED("PAUSED",  "暂停"),
    ACQUIRED("ACQUIRED", "正常执行"),
    BLOCKED("BLOCKED", "阻塞"),
    ERROR("ERROR", "错误"),
    COMPLETE("COMPLETE", "完成")
    ;

    private String code;

    private String value;


    private static Map<String,EnumTaskExecType> pool = new HashMap<String,EnumTaskExecType>();

    static {
        for (EnumTaskExecType each : EnumTaskExecType.values()) {
            EnumTaskExecType defined = pool.get(each.getCode());
            if(null != defined){
                pool.put(null, null);
            }
            pool.put(each.getCode(),each);
        }
    }


    EnumTaskExecType(String code, String value) {
        this.code = code;
        this.value = value;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
