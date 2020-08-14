package com.robot.host.common.constants;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

public enum EnumSysConfigType {

    Heart("heartBeatInterval", Long.class, null),
    RunData("robotRunInterval", Long.class, "runDataTask"),
    WeatherData("weatherInterval", Long.class, "weatherDataTask"),
    RegisterTimeOut("registerTimeOut", Long.class, null),
    CoordinateUnitLength("coordinateUnitLength", Long.class, null),
    DeviceFile("deviceFile",String.class, null),
    RobotFile("robotFile", String.class, null),
    ResultFile("resultFile", String.class, null)
    ;


    private String name;

    private Class clazz;

    private String beanName;

    private static Map<String,EnumSysConfigType> pool = new HashMap<String,EnumSysConfigType>();

    static {
        for (EnumSysConfigType each : EnumSysConfigType.values()) {
            EnumSysConfigType defined = pool.get(each.getName());
            if(null != defined){
                pool.put(null, null);
            }
            pool.put(each.getName(),each);
        }
    }

    EnumSysConfigType(String name, Class clazz, String beanName) {
        this.name = name;
        this.clazz = clazz;
        this.beanName = beanName;
    }

    public static EnumSysConfigType getEnum(String name){
        return pool.get(name);
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }
}
