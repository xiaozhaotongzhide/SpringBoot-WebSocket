package com.example.springbootwebsocket.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArraySet;

@Service
@Slf4j
@ServerEndpoint("/api/websocket/{sid}")
public class WebSocketServer {
    //静态变量,用来记录当前在线链接数,把他设计成一个线程安全的
    private static int onlineCount = 0;
    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    private static CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<WebSocketServer>();
    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;
    //接受的sid
    private String sid = "";

    /**
     * 建立成功连接调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) {
        this.session = session;
        //如果有重复直接返回
        for (WebSocketServer item : webSocketSet) {
            if (Objects.equals(item.sid, sid)) {
                return;
            }
        }
        webSocketSet.add(this);         //加入set中
        this.sid = sid;
        addOnlineCount();               //在线人数加1
        try {
            sendMessage("conn_success");
            log.info("有新窗口开始监听:" + sid + "当前在线人数为:" + getOnlineCount());
        } catch (IOException e) {
            log.error("websocket IO Exception");
        }
    }

    /**
     * 关闭连接的调用方法
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);   //从set中删除
        subOnlineCount();               //在线的人数减一
        //断开连接情况下,更新主板占用的情况为释放
        log.info("释放的isd为:" + sid);
        log.info("有一个连接关闭,当前在线的人数为" + getOnlineCount());
    }

    /**
     * 收到客户端调用的方法
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("来自窗口:" + sid + "的消息" + message);
        //群发消息
        for (WebSocketServer item : webSocketSet) {
            try {
                item.sendMessage("来自窗口:" + sid + "的消息" + message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 发生错误的时候
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发送错误");
        error.printStackTrace();
    }

    public static void sendInfo(String message) {
        for (WebSocketServer item : webSocketSet) {
            try {
                log.info("推送消息到窗口:" + item.sid + ",推送内容为:" + message);
                item.sendMessage(message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 群发自定义消息
     */
    public static void sendInfo(String message, @PathParam("sid") String sid) {
        log.info("推送消息到窗口:" + sid + ",推送内容为:" + message);
        for (WebSocketServer item : webSocketSet) {
            try {
                if (sid == null) {
                    item.sendMessage(message);
                } else if (item.sid.equals(sid)) {
                    item.sendMessage(message);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 实现服务器主动推送
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }

    public static CopyOnWriteArraySet<WebSocketServer> getWebSocketSet() {
        return webSocketSet;
    }
}
