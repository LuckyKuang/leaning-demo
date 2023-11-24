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

package com.luckykuang.client.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.websocket.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

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
 *      服务端维护ping消息发送，服务端就回复pong消息；
 *      服务端维护ping，只要服务端没有应答pong消息，就认为认为连接断开，断联后发起重连
 * @author luckykuang
 * @date 2023/9/6 16:58
 */
@Slf4j
@Component
@ClientEndpoint
public class JakartaWebSocketClientEndpoint {

    // 与某个服务端的连接会话，需要通过它来给服务端发送数据
    private Session session;

    // 订阅url，根据具体业务，也可以是用户id/用户名之类的
    @Value("${websocket.topic:topic}")
    private String topic;

    /**
     * 连接建立
     */
    @OnOpen
    public void onOpen(Session session) {
        log.info("websocket onOpen success");
    }

    /**
     * 连接关闭
     */
    @OnClose
    public void onClose(Session session, CloseReason reason) throws IOException {
        log.info("websocket onClose success");
        session.close();
    }

    /**
     * 接收文本信息
     */
    @OnMessage
    public void onMessage(Session session, String message) throws IOException {
        log.info("【websocket消息】收到服务端消息:" + message);
        if (StringUtils.isBlank(message)) {
            return;
        }
        // 此处做业务处理
        log.info("【websocket消息】收到服务端消息:" + message);
        session.getBasicRemote().sendText(message);
    }

    /**
     * 接收pong信息
     * 如果是服务端维护ping消息，客户端就无需添加此方法
     * 如果是客户端维护ping消息，客户端需要添加此方法【本客户端维护ping】
     */
    @OnMessage
    public void onMessage(Session session, PongMessage message) {
        String msg = new String(message.getApplicationData().array());
        log.info("【websocket消息】收到服务端Pong消息:" + msg);
    }

    /**
     * 接收二进制信息，也可以用byte[]接收
     */
    @OnMessage
    public void onMessage(Session session, ByteBuffer message) throws IOException {
        String msg = message.toString();
        log.info("【websocket消息】收到服务端Binary消息:" + msg);
        if (StringUtils.isBlank(msg)) {
            return;
        }
        // 此处做业务处理
        log.info("【websocket消息】收到服务端Binary消息:" + msg);
        session.getBasicRemote().sendBinary(message);
    }

    /**
     * 异常处理
     */
    @OnError
    public void onError(Session session, Throwable e) throws IOException {
        log.error("【websocket消息】消息反序列化失败", e);
        session.close();
    }

    /**
     * 主动发送消息给服务端
     */
    public void sendMessage(String message) throws IOException {
        if (session == null) {
            session = createConnect();
        }
        session.getBasicRemote().sendText(message);
    }

    /**
     * 向服务端发送ping心跳，服务端会自动回复pong消息
     */
    @PostConstruct
    private void sendPing() {
        new Thread(() -> {
            while (true){
                try {
                    if (session == null) {
                        session = createConnect();
                    }
                    TimeUnit.SECONDS.sleep(5);
                    session.getBasicRemote().sendPing(ByteBuffer.wrap("ok".getBytes()));
                } catch (Exception e){
                    log.error("websocket服务端异常，停止ping心跳发送");
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }).start();
    }

    /**
     * 创建连接
     */
    private Session createConnect(){
        URI uri = URI.create("ws://localhost:9022/websocket/" + topic);
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        try {
            return container.connectToServer(JakartaWebSocketClientEndpoint.class, uri);
        } catch (Exception e) {
            log.warn("websocket服务端连接异常...");
            throw new RuntimeException("websocket服务端连接异常",e);
        }
    }

    /**
     * 关闭连接
     */
    @PreDestroy
    private void closeConnect() throws IOException {
        session.close();
    }
}
