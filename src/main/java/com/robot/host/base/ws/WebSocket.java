package com.robot.host.base.ws;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/webSocket/{username}")
@Component
@Slf4j
public class WebSocket {

    private Session session;
    private String username;

    private static Map<String, WebSocket> clients = new ConcurrentHashMap<String, WebSocket>();


    @OnOpen
    public void onOpen(@PathParam("username") String username, Session session){
        this.session = session;
        this.username = username;

        clients.put(username,this);
        log.info("ws：已连接");
    }

    @OnClose
    public void onClose(){
        clients.remove(username);
    }

    @OnMessage
    public void onMessage(String message){
        JSONObject jsonObject = JSONObject.parseObject(message);
        String msg = (String) jsonObject.get("message");
        String to = (String) jsonObject.get("To");

//        System.out.println(msg);
        for (WebSocket ws : clients.values()) {
            if(ws.username.equals(to)){
                ws.session.getAsyncRemote().sendText(msg);
            }
        }

    }

    @OnError
    public void onError(Session session, Throwable error){
        error.printStackTrace();
    }

    public static Map<String, WebSocket> getClients() {
        return clients;
    }
}
