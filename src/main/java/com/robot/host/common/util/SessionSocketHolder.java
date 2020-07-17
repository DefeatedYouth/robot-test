package com.robot.host.common.util;

import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 22/05/2018 18:33
 * @since JDK 1.8
 */
public class SessionSocketHolder {
    private static final Map<String, NioSocketChannel> CHANNEL_MAP = new ConcurrentHashMap<>(16);
    private static final String CLIENT_CHANNEL = "CLIENT_CHANNEL";//客户端的channel;

    public static void put(String robotCode, NioSocketChannel socketChannel) {
        CHANNEL_MAP.put(robotCode, socketChannel);
    }

    public static NioSocketChannel get(String robotCode) {
        return CHANNEL_MAP.get(robotCode);
    }

    public static Map<String, NioSocketChannel> getMAP() {
        return CHANNEL_MAP;
    }

    public static void remove(NioSocketChannel nioSocketChannel) {
        CHANNEL_MAP.entrySet().stream().filter(entry -> entry.getValue() == nioSocketChannel).forEach(entry -> CHANNEL_MAP.remove(entry.getKey()));
    }

    public static void removeByRobotCode(String robotCode) {
        CHANNEL_MAP.remove(robotCode);
    }

    public static void putClient(NioSocketChannel socketChannel) {
        CHANNEL_MAP.put(CLIENT_CHANNEL, socketChannel);
    }

    public static NioSocketChannel getClient() {
        return CHANNEL_MAP.get(CLIENT_CHANNEL);
    }


}
