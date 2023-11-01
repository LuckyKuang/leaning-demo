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

package com.luckykuang.netty.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import static com.luckykuang.netty.util.MqttServerUtils.*;

/**
 * @author luckykuang
 * @date 2023/10/30 15:22
 */
@Slf4j
public class MqttServerHandler extends SimpleChannelInboundHandler<MqttMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MqttMessage msg) throws Exception {
        Channel channel = ctx.channel();
        String payload = String.valueOf(msg.payload());
        MqttMessageType mqttMessageType = msg.fixedHeader().messageType();
        try {
            // 处理不同类型的MQTT消息
            switch (mqttMessageType){
                case CONNECT -> {
                    log.info("连接服务端：{}",msg);
                    connect(channel,msg);
                }
                case PUBLISH -> {
                    log.info("发布消息：{}",msg);
                    publish(channel,msg);
                }
                case PUBACK -> {
                    log.info("发布确认：{}",msg);
                    puback(channel,msg);
                }
                case PUBREC -> {
                    log.info("发布收到（QoS 2，第一步）：{}",msg);
                    pubrec(channel,msg);
                }
                case PUBREL -> {
                    log.info("发布释放（QoS 2，第二步）：{}",msg);
                    pubrel(channel,msg);
                }
                case PUBCOMP -> {
                    log.info("发布完成（QoS 2，第三步）：{}",msg);
                    pubcomp(channel,msg);
                }
                case SUBSCRIBE -> {
                    log.info("订阅主题：{}",msg);
                    subscribe(channel,msg);
                }
                case UNSUBSCRIBE -> {
                    log.info("取消订阅：{}",msg);
                    unSubscribe(channel,msg);
                }
                case PINGREQ -> {
                    log.info("心跳请求：{}",msg);
                    pingReq(channel,msg);
                }
                case DISCONNECT -> {
                    log.info("断开连接：{}",msg);
                    disConnect(channel,msg);
                }
                default -> {
                    log.info("非法类型：{}，非法信息：{}",mqttMessageType,payload);
                }
            }
        } catch (Exception e){
            log.error("netty server is error",e);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("exceptionCaught",cause);
        if (cause instanceof IOException){
            ctx.close();
        }
        super.exceptionCaught(ctx,cause);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent idleStateEvent) {
            if (idleStateEvent.state() == IdleState.ALL_IDLE) {
                log.info("userEventTriggered 读写超时通道{}关闭",ctx.channel());
                // 关闭通道
                ctx.channel().close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
