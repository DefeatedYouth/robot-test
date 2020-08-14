package com.robot.host.common.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 系统配置
 */
public class SysConfigUtil {

    private static final Map<String, String> CONFIG_MAP = new ConcurrentHashMap<>(50);

    public static void put(String sysName, String sysValue){
        CONFIG_MAP.put(sysName, sysValue);
    }

    public static String get(String sysName){
        return CONFIG_MAP.get(sysName);
    }

    public static Map<String, String> getMap(){
        return CONFIG_MAP;
    }

    public static void remove(String sysName){
        CONFIG_MAP.entrySet().stream().filter(entry -> entry.getKey() == sysName).forEach(entry -> CONFIG_MAP.remove(entry.getKey()));
    }
}
