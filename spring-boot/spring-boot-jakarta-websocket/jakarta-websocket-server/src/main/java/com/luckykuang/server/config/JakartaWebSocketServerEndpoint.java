/*
 * Copyright 2015-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.luckykuang.server.config;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * websocket实现类
 * session使用API：
 *      //发送文本消息
 *      session.getBasicRemote().sendText(String message);
 *      //发送二进制消息
 *      session.getBasicRemote().sendBinary(ByteBuffer message);
 *      //发送对象消息，会尝试使用Encoder编码
 *      session.getBasicRemote().sendObject(Object message);
 *      //发送ping
 *      session.getBasicRemote().sendPing(ByteBuffer buffer);
 *      //发送pong
 *      session.getBasicRemote().sendPong(ByteBuffer buffer);
 * ping/pong详解：
 *      ping和pong是用来维护tcp心跳的。
 *      客户端维护ping消息发送，服务端就回复pong消息；
 *      客户端维护ping，只要服务端没有应答pong消息，就认为认为连接断开，断联后发起重连
 * 注意：
 *      getAsyncRemote()是异步发送，getBasicRemote()是同步发送，存在并发情况下，一定要用同步发送，异步会报错 "TEXT_FULL_WRITING"
 *      发送消息是锁住webSockets，也是因为存在高并发情况下，解决 "TEXT_FULL_WRITING" 报错问题
 * @author luckykuang
 * @date 2023/9/6 16:58
 */
@Slf4j
@Component
@ServerEndpoint("/websocket/{topic}") // 接口路径：ws://localhost:9022/websocket/topic
public class JakartaWebSocketServerEndpoint {

    // 与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;
    // 连接topic，根据业务来定，也可以是用户id或者其他
    private String topic;
    // JakartaWebSocketServerEndpoint是当前类名
    private static final Map<String, JakartaWebSocketServerEndpoint> webSockets = new ConcurrentHashMap<>();
    // 用来存在线连接用户信息
    private static final Map<String, Session> sessionPool = new ConcurrentHashMap<>();

    /**
     * 连接建立
     */
    @OnOpen
    public void onOpen(Session session, EndpointConfig config, @PathParam(value = "topic") String topic) {
        try {
            this.session = session;
            this.topic = topic;
            webSockets.put(session.getId(), this);
            sessionPool.put(session.getId(), session);
            log.info("【websocket消息】有新的连接，总数为:" + webSockets.size());
        } catch (Exception e) {
            log.error("websocket onOpen exception:", e);
        }
    }

    /**
     * 连接关闭
     */
    @OnClose
    public void onClose(Session session, CloseReason reason) {
        try {
            webSockets.remove(session.getId());
            sessionPool.remove(session.getId());
            log.info("【websocket消息】连接断开，总数为:" + webSockets.size());
        } catch (Exception e) {
            log.error("websocket onClose exception:", e);
        }
    }

    /**
     * 接收文本信息
     */
    @OnMessage
    public void onMessage(Session session, String message) {
        log.info("【websocket消息】收到客户端消息:" + message);
        if (StringUtils.isBlank(message)) {
            return;
        }
        // 此处做业务处理
        log.info("【websocket消息】收到客户端消息:" + message);
    }

    /**
     * 接收pong信息
     * 如果是服务端维护ping消息，客户端就无需添加此方法
     * 如果是客户端维护ping消息，客户端需要添加此方法【本案例客户端维护ping，故服务端无需维护此方法】
     */
//    @OnMessage
//    public void onMessage(Session session, PongMessage message) {
//        String msg = new String(message.getApplicationData().array());
//        log.info("【websocket消息】收到客户端Pong消息:" + msg);
//    }

    /**
     * 接收二进制信息，也可以用byte[]接收
     */
    @OnMessage
    public void onMessage(Session session, ByteBuffer message) {
        String msg = message.toString();
        log.info("【websocket消息】收到客户端Binary消息:" + msg);
        if (StringUtils.isBlank(msg)) {
            return;
        }
        // 此处做业务处理
        log.info("【websocket消息】收到客户端Binary消息:" + msg);
    }

    /**
     * 异常处理
     */
    @OnError
    public void onError(Session session, Throwable e) {
        //异常处理
        webSockets.remove(session.getId());
        sessionPool.remove(session.getId());
        log.error("【websocket消息】[{}]消息反序列化失败", topic, e);
    }

    /**
     * 此为广播消息 - 发送给所有客户端
     */
    public void sendAllMessage(String message) {
        log.info("【websocket消息】广播消息:" + message);
        for (JakartaWebSocketServerEndpoint webSocket : webSockets.values()) {
            try {
                synchronized (webSockets) {
                    if (webSocket.session.isOpen()) {
                        webSocket.session.getBasicRemote().sendText(message);
                    }
                }
            } catch (Exception e) {
                log.error("【websocket消息】 广播消息异常：", e);
            }
        }
    }

    /**
     * 此为广播消息 - 发送给所有订阅了该topic的客户端
     */
    public void sendTopicMessage(String topic, String message) {
        log.info("【websocket消息】topic消息:{},topic:{}", message, topic);
        List<JakartaWebSocketServerEndpoint> webSocketList = webSockets.values().stream()
                .filter(webSocket -> Objects.equals(webSocket.topic, topic)).toList();
        if (!webSocketList.isEmpty()) {
            for (JakartaWebSocketServerEndpoint webSocket : webSocketList) {
                try {
                    synchronized (webSockets) {
                        if (webSocket.session.isOpen()) {
                            log.info("【websocket消息】topic消息开始发送 topic:{},msg:{}", webSocket.topic, message);
                            webSocket.session.getBasicRemote().sendText(message);
                        }
                    }
                } catch (Exception e) {
                    log.error("【websocket消息】 广播消息异常：", e);
                }
            }
        }
    }

    /**
     * 此为单点消息
     */
    public void sendOneMessage(Session session, String message) {
        Session getSession = sessionPool.get(session.getId());
        if (getSession != null && getSession.isOpen()) {
            try {
                synchronized (webSockets) {
                    log.info("【websocket消息】 单点消息:" + message);
                    getSession.getBasicRemote().sendText(message);
                }
            } catch (Exception e) {
                log.error("【websocket消息】 单点消息异常：", e);
            }
        }
    }
}
