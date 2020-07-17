package com.robot.host.common.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: com.face.ele.smart.modules.netty.utils
 * @description: 机器人传输协议的消息类型----操控机器人
 * @author: dadi
 * @create: 2020-05-07 16:17
 */
public enum EnumRobotTaskControlType {
    /**
     * <41>: = 任务控制 <1>: = 任务启动
     * <2>: = 任务暂停
     * <3>: = 任务继续
     * <4>: = 任务停止
     */
    //机器人车体
    START("41-1", 41, 1, "任务启动"),
    PAUSE("41-2", 41, 2, "任务暂停"),
    CONTINUE("41-3", 41, 3, "任务继续"),
    STOP("41-4", 41, 4, "任务停止");


    private String fullCode;//大类加子类
    private Integer command;//子类
    private Integer type;//大类
    private String text;

    private static Map<String, EnumRobotTaskControlType> pool = new HashMap<String, EnumRobotTaskControlType>();

    static {
        for (EnumRobotTaskControlType each : EnumRobotTaskControlType.values()) {
            EnumRobotTaskControlType defined = pool.get(each.getFullCode());
            if (null != defined) {
                pool.put(null, null);
            }
            pool.put(each.fullCode, each);
        }
    }


    EnumRobotTaskControlType(String code, Integer type, Integer command, String message) {
        this.fullCode = code;
        this.type = type;
        this.command = command;
        this.text = message;
    }

    public static EnumRobotTaskControlType getEnum(String code) {
        return pool.get(code);
    }


    public String getText() {
        return text;
    }

    public String getFullCode() {
        return fullCode;
    }

    public void setFullCode(String fullCode) {
        this.fullCode = fullCode;
    }

    public Integer getCommand() {
        return command;
    }

    public void setCommand(Integer command) {
        this.command = command;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
