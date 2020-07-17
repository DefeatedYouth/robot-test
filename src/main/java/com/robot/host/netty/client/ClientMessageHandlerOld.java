package com.robot.host.netty.client;

import com.robot.host.common.constants.MessageTypeEnum;
import com.robot.host.common.constants.ProtocolMessage;
//import com.robot.host.common.dto.XmlInRobotRunDTO;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @program: com.face.ele.smart.modules.netty.client
 * @description:
 * @author: dadi
 * @create: 2020-05-04 11:07
 */
@Slf4j
public class ClientMessageHandlerOld extends SimpleChannelInboundHandler<ProtocolMessage> {

    // 创建一个线程，模拟用户发送消息
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 对于客户端，在建立连接之后，在一个独立线程中模拟用户发送数据给服务端
        executor.execute(new MessageSender(ctx));
    }

    /**
     * 这里userEventTriggered()主要是在一些用户事件触发时被调用，这里我们定义的事件是进行心跳检测的
     * ping和pong消息，当前触发器会在指定的触发器指定的时间返回内如果客户端没有被读取消息或者没有写入
     * 消息到管道，则会触发当前方法
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                // 一定时间内，当前服务没有发生读取事件，也即没有消息发送到当前服务来时，
                // 其会发送一个Ping消息到服务器，以等待其响应Pong消息
                ProtocolMessage message = new ProtocolMessage();
                message.setMessageType(MessageTypeEnum.PING);
                ctx.writeAndFlush(message);
            } else if (event.state() == IdleState.WRITER_IDLE) {
                // 如果当前服务在指定时间内没有写入消息到管道，则关闭当前管道
                ctx.close();
            }
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ProtocolMessage msg) throws Exception {
        log.info("客户端读取了消息{}", msg);
    }

    private static final class MessageSender implements Runnable {

        private static final AtomicLong counter = new AtomicLong(1);
        private volatile ChannelHandlerContext ctx;

        public MessageSender(ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    // 模拟随机发送消息的过程

                    log.info("客户端发送消息第{}", counter.getAndIncrement());
                    ProtocolMessage message = getRegisterMessage();
                    //ProtocolMessage service = getStatusMessage();
                    // ProtocolMessage service = getRunMessage();
                    // ProtocolMessage service =getWeatherMessage();
                    ChannelFuture future = ctx.writeAndFlush(message);

                    future.addListener((ChannelFutureListener) future1 -> {
                        if (future1.isSuccess()) {
                            log.info("发送成功");
                        } else {
                            log.info("发送失败断开连接");
                            ctx.channel().close();
                            throw new Exception("发送失败断开连接");
                        }
                    });
                    TimeUnit.SECONDS.sleep(111);
                    //System.out.println("客户端发送消息message"+service);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.error(e.getMessage());
            }
        }
    }

    public static ProtocolMessage getRegisterMessage() {
        ProtocolMessage message = new ProtocolMessage();
        message.setBody("<Robot>\n" +
                "<SendCode>Client01</SendCode>\n" +
                "<ReceiveCode>Server01</ReceiveCode>\n" +
                "<Type>251</Type>\n" +
                "<Code />\n" +
                "<Command >1</Command>\n" +
                "<Time>" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "</Time>\n" +
                "<Items/>\n" +
                "</Robot>\n");
        message.setSessionId(1L);
        return message;
    }

    public static void main(String[] args) {
     /*  ProtocolMessage ms= getStatusMessage();
       String s=ms.getBody();
        XmlInRobotPushCommonDTO xx=  XmlBeanUtils.xmlToBean(s, XmlInRobotPushCommonDTO.class);
        System.out.println(xx);
        ProtocolMessage ms1= getRegisterMessage();
        String s1=ms1.getBody();
        RegisterInXmlDTO xx1=  XmlBeanUtils.xmlToBean(s1, RegisterInXmlDTO.class);
        System.out.println(xx1);*/
//        XmlInRobotRunDTO sss = new XmlInRobotRunDTO();
//        sss.setCode("1111");
//        XmlInRobotRunDTO.Item item = new XmlInRobotRunDTO.Item();
//        item.setRobotCode("1111");
//        item.setRobotName("222");
//        sss.setItems(Lists.newArrayList(item));
//        String out = XmlBeanUtils.beanToXml(sss, XmlInRobotRunDTO.class);
//        System.out.println(out);
    }

    private static ProtocolMessage getStatusMessage() {
        ProtocolMessage message = new ProtocolMessage();
        message.setBody("<Robot>\n" +
                "<SendCode>Client01</SendCode>\n" +
                "<ReceiveCode>Server01</ReceiveCode>\n" +
                "<Type>1</Type>\n" +
                "<Code />\n" +
                "<Command >1</Command>\n" +
                "<Time>2020-05-06 13:56:25</Time>\n" +
                "<Items>\n" +
                "<Item robot_name=\"testRobot1\" robot_code=\"Robot1\" time=\"2020-05-06 13:56:25\" type=\"1\" value=\"1.2\" value_unit=\"m/s\" unit=\"m/s\" />\n" +
                "<Item robot_name=\"testRobot1\" robot_code=\"Robot1\" time=\"2020-05-06 13:56:25\" type=\"2\" value=\"100\" value_unit=\"m\" unit=\"m\" />\n" +
                "<Item robot_name=\"testRobot1\" robot_code=\"Robot1\" time=\"2020-05-06 13:56:25\" type=\"3\" value=\"29\" value_unit=\"V\" unit=\"V\" />\n" +
                "</Items>\n" +
                "</Robot>\n");

//        service.setBody("<Robot>\n" +
//                "<SendCode>Client01</SendCode>\n" +
//                "<ReceiveCode>Server01</ReceiveCode>\n" +
//                "<Type>1</Type>\n" +
//                "<Code/>\n" +
//                "<Command>1</Command>\n" +
//                "<Time>2020-05-06 13:56:25</Time>\n" +
//                "<Items>\n" +
//                "<Item robot_name=\"testRobot1\" robot_code=\"Robot1\" time=\"2020-05-06 13:56:25\" type=\"1\" value=\"1\" value_unit=\"\" unit=\"m/s\" />\n" +
//                "<Item robot_name=\"testRobot1\" robot_code=\"Robot1\" time=\"2020-05-06 13:56:25\" type=\"2\" value=\"1\" value_unit=\"\" unit=\"m\" />\n" +
//                "<Item robot_name=\"testRobot1\" robot_code=\"Robot1\" time=\"2020-05-06 13:56:25\" type=\"3\" value=\"1\" value_unit=\"\" unit=\"V\" />\n" +
//                "</Items>\n" +
//                "</Robot>\n");
        return message;
    }

    private static ProtocolMessage getRunMessage() {
        ProtocolMessage message = new ProtocolMessage();
        message.setBody("<Robot>\n" +
                "<SendCode>Client01</SendCode>\n" +
                "<ReceiveCode>Server01</ReceiveCode>\n" +
                "<Type>2</Type>\n" +
                "<Code />\n" +
                "<Command >1</Command>\n" +
                "<Time>2020-05-06 13:56:25</Time>\n" +
                "<Items>\n" +
                "<Item robot_name=\"testRobot1\" robot_code=\"Robot1\" time=\"2020-05-06 13:56:25\" type=\"1\" value=\"11.0\" value_unit=\"m/s\" unit=\"m/s\" />\n" +
                "<Item robot_name=\"testRobot1\" robot_code=\"Robot1\" time=\"2020-05-06 13:56:25\" type=\"2\" value=\"12\" value_unit=\"m\" unit=\"m\" />\n" +
                "<Item robot_name=\"testRobot1\" robot_code=\"Robot1\" time=\"2020-05-06 13:56:25\" type=\"3\" value=\"13\" value_unit=\"V\" unit=\"V\" />\n" +
                "</Items>\n" +
                "</Robot>\n");
        return message;
    }

    private static ProtocolMessage getWeatherMessage() {
        ProtocolMessage message = new ProtocolMessage();
        message.setBody("<Robot>\n" +
                "<SendCode>Client01</SendCode>\n" +
                "<ReceiveCode>Server01</ReceiveCode>\n" +
                "<Type>21</Type>\n" +
                "<Code />\n" +
                "<Command>1</Command>\n" +
                "<Time >2020-05-06 13:56:25</Time>\n" +
                "<Items >\n" +
                "<Item robot_name=\"testRobot1\" robot_code=\"Robot1\" time=\"2020-05-06 13:56:25\" type=\"1\" value=\"11.0\" value_unit=\"度\" unit=\"m/s\" />\n" +
                "<Item robot_name=\"testRobot1\" robot_code=\"Robot1\" time=\"2020-05-06 13:56:25\" type=\"2\" value=\"12\" value_unit=\"m\" unit=\"m\" />\n" +
                "<Item robot_name=\"testRobot1\" robot_code=\"Robot1\" time=\"2020-05-06 13:56:25\" type=\"3\" value=\"13\" value_unit=\"V\" unit=\"V\" />\n" +
                "</Items>\n" +
                "</Robot>\n");
        return message;
    }
}
