package com.robot.host.netty.resolver.out;

import com.robot.host.common.dto.MessageAboutRobotDTO;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @program: com.face.ele.smart.modules.netty.xml
 * @description:主动发送请求的处理工厂
 * @author: dadi
 * @create: 2020-05-04 11:06
 */
@Slf4j
public class MessageResolverOutFactory {
    // 创建一个工厂类实例
    private static final MessageResolverOutFactory resolverFactory = new MessageResolverOutFactory();
    private static final List<OutResolver> resolvers = new CopyOnWriteArrayList<>();

    private MessageResolverOutFactory() {
    }

    // 使用单例模式实例化当前工厂类实例
    public static MessageResolverOutFactory getInstance() {
        return resolverFactory;
    }

    public void registerResolver(OutResolver resolver) {
        resolvers.add(resolver);
    }

    // 根据解码后的消息，在工厂类处理器中查找可以处理当前消息的处理器
    public OutResolver getMessageResolver(MessageAboutRobotDTO message) {
        for (OutResolver resolver : resolvers) {
            if (resolver.support(message)) {
                return resolver;
            }
        }
        throw new RuntimeException("【巡视主机业务】cannot find out resolver, service body: {}" + message);
    }

}
