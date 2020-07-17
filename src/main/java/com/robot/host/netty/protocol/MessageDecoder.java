package com.robot.host.netty.protocol;

import com.robot.host.common.constants.NettyConstants;
import com.robot.host.common.constants.ProtocolMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @program: com.face.ele.smart.modules.netty.service.decoder
 * @description:
 * @author: dadi
 * @create: 2020-05-04 11:01
 */
@Slf4j
public class MessageDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {

        ProtocolMessage message = new ProtocolMessage();
        byte[] startMagic = new byte[2];
        byteBuf.order(ByteOrder.LITTLE_ENDIAN).readBytes(startMagic);
        if (startMagic[0] != NettyConstants.magicNumber[0]||
                startMagic[1] != NettyConstants.magicNumber[1]  ) {
            log.error("发送的数据格式start魔数异常{}", startMagic);
             byteBuf.release();
            return;
        }
        //service.setMagicNumber(startMagic);  // 读取魔数
        long sessionId = byteBuf.readLongLE();    // 读取sessionId
        message.setSessionId(sessionId);

        //service.setMessageType(MessageTypeEnum.get(byteBuf.readByte()));	// 读取当前的消息类型

        int bodyLength = byteBuf.readIntLE();    // 读取消息体长度和数据
        if (bodyLength > 0) {
            CharSequence body = byteBuf.readCharSequence(bodyLength, Charset.defaultCharset());
            message.setBody(body.toString());
        }
//        log.info("接收到的数内容为{}", message.getBody());
        byte[] endMagic = new byte[2];
        byteBuf.order(ByteOrder.LITTLE_ENDIAN).readBytes( endMagic);
        if (startMagic[0] != NettyConstants.magicNumber[0]||
                startMagic[1] != NettyConstants.magicNumber[1]  ) {
            log.error("发送的数据格式end魔数异常{}", endMagic);
            byteBuf.release();
            return;
        }
        out.add(message);
    }
}
