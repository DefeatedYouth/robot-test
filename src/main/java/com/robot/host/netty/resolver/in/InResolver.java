package com.robot.host.netty.resolver.in;

import com.robot.host.common.dto.MessageJudgeInDTO;
import com.robot.host.common.constants.ProtocolMessage;
import io.netty.channel.ChannelHandlerContext;

public interface InResolver {
    boolean support(MessageJudgeInDTO judgeInDTO);

    ProtocolMessage resolve(ProtocolMessage message, ChannelHandlerContext ctx);
}
