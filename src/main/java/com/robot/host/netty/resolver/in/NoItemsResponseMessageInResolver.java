package com.robot.host.netty.resolver.in;

import com.robot.host.common.constants.NettyConstants;
import com.robot.host.common.constants.ProtocolMessage;
import com.robot.host.common.dto.MessageJudgeInDTO;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * 巡视主机无内容响应
 */
@Slf4j
public class NoItemsResponseMessageInResolver implements InResolver {
    @Override
    public boolean support(MessageJudgeInDTO judgeInDTO) {
        if (!StringUtils.equals(judgeInDTO.getType(), NettyConstants.IN_OUT_CODE_SYSTEM_TYPE + "")) {
            return false;
        }
        if (!StringUtils.equals(judgeInDTO.getCommand(), NettyConstants.OUT_CODE_SYSTEM_TYPE_COMMAND_COMMON_NO_ITEMS + "")) {
            return false;
        }
        return true;
    }

    @Override
    public ProtocolMessage resolve(ProtocolMessage message, ChannelHandlerContext ctx) {
        log.info("无Item响应");
        return null;
    }
}
