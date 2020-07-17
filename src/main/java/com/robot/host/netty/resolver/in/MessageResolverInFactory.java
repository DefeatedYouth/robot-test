package com.robot.host.netty.resolver.in;

import cn.hutool.core.util.XmlUtil;
import com.robot.host.common.dto.MessageJudgeInDTO;
import com.robot.host.common.constants.ProtocolMessage;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;

import javax.xml.xpath.XPathConstants;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @program: com.face.ele.smart.modules.netty.xml
 * @description: 收到请求的处理工厂
 * @author: dadi
 * @create: 2020-05-04 11:06
 */
@Slf4j
public class MessageResolverInFactory {
    // 创建一个工厂类实例
    private static final MessageResolverInFactory resolverFactory = new MessageResolverInFactory();
    private static final List<InResolver> resolvers = new CopyOnWriteArrayList<>();

    private MessageResolverInFactory() {
    }

    // 使用单例模式实例化当前工厂类实例
    public static MessageResolverInFactory getInstance() {
        return resolverFactory;
    }

    public void registerResolver(InResolver resolver) {
        resolvers.add(resolver);
    }

    // 根据解码后的消息，在工厂类处理器中查找可以处理当前消息的处理器
    public InResolver getMessageResolver(ProtocolMessage message) {
        MessageJudgeInDTO dto = this.getMessageJudgeInDTO(message);
        for (InResolver resolver : resolvers) {
            if (resolver.support(dto)) {
                return resolver;
            }
        }
        throw new RuntimeException("【巡视主机业务】cannot find In resolver, service body: {}" + message.getBody());
    }

    /**
     * 得到具体消息需要判断的类型
     */
    private MessageJudgeInDTO getMessageJudgeInDTO(ProtocolMessage message) {
        String body = message.getBody();
        Document docResult = XmlUtil.parseXml(body);
        //结果为“ok”
        Object robotTypeObj = XmlUtil.getByXPath("//Robot/Type", docResult, XPathConstants.STRING);
        if (robotTypeObj == null) {
            log.error("xml内容的类型为空{}", body);
//            throw new MxException("类型为空");
            return null;
        }
        MessageJudgeInDTO messageJudgeDTO = new MessageJudgeInDTO();
        String robotType = robotTypeObj.toString();
        messageJudgeDTO.setType(robotType);

        Object commandObj = XmlUtil.getByXPath("//Robot/Command", docResult, XPathConstants.STRING);
        if (commandObj != null) {
//            log.error("xml内容的command为空", body);
//            throw new MxException("command为空");
            messageJudgeDTO.setCommand(commandObj.toString());
        }
        return messageJudgeDTO;
    }
}
