package com.robot.host.netty.protocol;

import com.robot.host.common.constants.NettyConstants;
import com.robot.host.common.constants.ProtocolMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.ByteOrder;
import java.nio.charset.Charset;

/**
 * @program: com.face.ele.smart.modules.netty.service.encoder
 * @description:
 * @author: dadi
 * @create: 2020-05-04 10:58
 */
public class MessageEncoder extends MessageToByteEncoder<ProtocolMessage> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ProtocolMessage message, ByteBuf out) {
        // 这里会判断消息类型是不是EMPTY类型，如果是EMPTY类型，则表示当前消息不需要写入到管道中
        out.order(ByteOrder.LITTLE_ENDIAN).writeBytes(NettyConstants.magicNumber);    // 写入当前的魔数
        // 生成一个sessionId，并将其写入到字节序列中
        // String sessionId = SessionIdGenerator.generateSessionId();
        long sessionId = message.getSessionId();
        message.setSessionId(sessionId);
        out.writeLongLE(sessionId);
        if (null == message.getBody()) {
            out.writeIntLE(0);    // 如果消息体为空，则写入0，表示消息体长度为0
        } else {
            out.writeIntLE(message.getBody().getBytes().length);
            out.writeCharSequence(message.getBody(), Charset.defaultCharset());
        }
        //结束标记
        out.order(ByteOrder.LITTLE_ENDIAN).writeBytes(NettyConstants.magicNumber);    // 写入当前的魔数
    }

}
