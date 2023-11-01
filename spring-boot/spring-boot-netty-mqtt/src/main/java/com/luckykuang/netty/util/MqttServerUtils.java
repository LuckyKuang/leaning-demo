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

package com.luckykuang.netty.util;

import com.luckykuang.netty.config.MqttServerConfig;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static io.netty.handler.codec.mqtt.MqttQoS.*;

/**
 * @author luckykuang
 * @date 2023/10/31 15:14
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MqttServerUtils {

    private static final Map<String,Channel> CHANNEL_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, Set<String>> TOPIC_CACHE = new ConcurrentHashMap<>();

    public static void connect(Channel channel, MqttMessage msg) {
        log.info("Local Service MQTT-IP：" + channel.localAddress());
        log.info("Remote Client MQTT-IP：" + channel.remoteAddress());
        if (msg instanceof MqttConnectMessage mqttConnectMessage) {
            MqttServerConfig mqttServerConfig = ApplicationContextUtils.getBean(MqttServerConfig.class);
            // 校验用户名和密码
            String userName = mqttConnectMessage.payload().userName();
            String password = new String(mqttConnectMessage.payload().passwordInBytes(), StandardCharsets.UTF_8);
            if (mqttServerConfig.getUsername().equals(userName) && mqttServerConfig.getPassword().equals(password)) {
                log.info("校验用户名和密码通过 channelId:{},username:{},password:{}", channel.id().asLongText(), userName, password);
                // 校验通过，需要答复
                MqttConnAckMessage okResp = (MqttConnAckMessage) MqttMessageFactory.newMessage(
                        new MqttFixedHeader(MqttMessageType.CONNACK, false, AT_LEAST_ONCE, false, 0),
                        new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_ACCEPTED, true), null);
                channel.writeAndFlush(okResp);
                CHANNEL_CACHE.put(channel.id().asLongText(), channel);
            } else {
                log.info("校验用户名和密码失败 channelId:{},username:{},password:{}", channel.id().asLongText(), userName, password);
                channel.close();
            }
        }
    }

    public static void publish(Channel channel, MqttMessage msg) {
        log.info("Local Service MQTT-IP：" + channel.localAddress());
        log.info("Remote Client MQTT-IP：" + channel.remoteAddress());
        if (msg instanceof MqttPublishMessage mqttPublishMessage) {
            // 订阅主题
            String topic = mqttPublishMessage.variableHeader().topicName();
            // 发布内容
            ByteBuf byteBuf = mqttPublishMessage.content().duplicate();
            byte[] bytes = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(bytes);
            String content = new String(bytes, StandardCharsets.UTF_8);
            log.info("topic:{},消息:{}",topic,content);
            // 校验传入的数据是否符合要求
            if (StringUtils.isBlank(content)) {
                log.error("MQTT received an empty packet===》{}", content);
                publishBack(channel, mqttPublishMessage, "MQTT received an empty packet");
                return;
            }
            // 如果是qos1或者qos2类型都需要响应
            publishBack(channel, mqttPublishMessage, content);
            // 推送主题消息
            if (AT_LEAST_ONCE == mqttPublishMessage.fixedHeader().qosLevel()
                    || EXACTLY_ONCE == mqttPublishMessage.fixedHeader().qosLevel()) {
                Channel sendChannel = CHANNEL_CACHE.get(channel.id().asLongText());
                if (sendChannel != null){
                    MqttPublishMessage pubMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
                            new MqttFixedHeader(MqttMessageType.PUBLISH, false,
                                    AT_LEAST_ONCE, false, 0),
                            new MqttPublishVariableHeader(topic, mqttPublishMessage.variableHeader().packetId()),
                            Unpooled.buffer().writeBytes(content.getBytes(StandardCharsets.UTF_8)));
                    channel.writeAndFlush(pubMessage);
                }else {
                    channel.close();
                }
            }
        }
    }

    private static void publishBack(Channel channel, MqttPublishMessage msg, String payLoad) {
        if (AT_MOST_ONCE == msg.fixedHeader().qosLevel()) {
            // qos0消息类型,不需要ACK客户端
            return;
        }
        if (AT_LEAST_ONCE == msg.fixedHeader().qosLevel()) {
            // qos1消息类型,需要向客户端返回MqttMessageType.PUBACK 类型ACK应答
            MqttPubAckMessage sendMessage = (MqttPubAckMessage) MqttMessageFactory.newMessage(
                    new MqttFixedHeader(MqttMessageType.PUBACK, false,
                            AT_LEAST_ONCE, false, 0),
                    MqttMessageIdVariableHeader.from(msg.variableHeader().packetId()), payLoad);
            channel.writeAndFlush(sendMessage);
            return;
        }
        if (EXACTLY_ONCE == msg.fixedHeader().qosLevel()) {
            // qos2消息类型
            MqttMessage mqttMessage = MqttMessageFactory.newMessage(
                    new MqttFixedHeader(MqttMessageType.PUBREC, false,
                            EXACTLY_ONCE, false, 0),
                    MqttMessageIdVariableHeader.from(msg.variableHeader().packetId()), payLoad);
            channel.writeAndFlush(mqttMessage);
        }
    }

    public static void subscribe(Channel channel, MqttMessage msg) {
        if (msg instanceof MqttSubscribeMessage mqttSubscribeMessage) {
            // 订阅主题
            List<MqttTopicSubscription> topicSubscriptionsList = mqttSubscribeMessage.payload().topicSubscriptions();
            String channelId = channel.id().asLongText();
            log.info("subscribe channelId:[{}],TopicList:[{}]", channelId, topicSubscriptionsList);
            // 存放topic
            for (MqttTopicSubscription mqttTopicSubscription : topicSubscriptionsList) {
                String topicName = mqttTopicSubscription.topicName();
                // 队列缓存
                Set<String> topicList = new HashSet<>();
                // 存在
                if (TOPIC_CACHE.get(topicName) != null && !TOPIC_CACHE.isEmpty()) {
                    topicList = TOPIC_CACHE.get(topicName);
                }
                topicList.add(channelId);
                TOPIC_CACHE.put(topicName, topicList);
            }
            // 响应
            MqttQoS mqttQoS = mqttSubscribeMessage.fixedHeader().qosLevel();
            MqttSubAckMessage subAckMessage = (MqttSubAckMessage) MqttMessageFactory.newMessage(
                    new MqttFixedHeader(MqttMessageType.SUBACK, false,
                            mqttQoS, false, 0),
                    MqttMessageIdVariableHeader.from(mqttSubscribeMessage.variableHeader().messageId()),
                    new MqttSubAckPayload(0));
            channel.writeAndFlush(subAckMessage);
        }
    }

    public static void unSubscribe(Channel channel, MqttMessage msg2) {
        if (msg2 instanceof MqttUnsubscribeMessage mqttUnsubscribeMessage) {
            // 订阅主题
            List<String> topicList = mqttUnsubscribeMessage.payload().topics();
            String channelId = channel.id().asLongText();
            log.info("unSubscribe channelId:[{}],TopicList:[{}]", channelId, topicList);
            // 存放topic
            for (String topicName : topicList) {
                // 存在
                if (TOPIC_CACHE.get(topicName) != null && !TOPIC_CACHE.isEmpty()) {
                    Set<String> topicSet = TOPIC_CACHE.get(topicName);
                    topicSet.remove(channelId);
                    // 更新
                    if (topicSet.isEmpty()) {
                        TOPIC_CACHE.remove(topicName);
                    } else {
                        TOPIC_CACHE.put(topicName, topicSet);
                    }
                }
            }
            // 响应
            MqttQoS mqttQoS = mqttUnsubscribeMessage.fixedHeader().qosLevel();
            MqttUnsubAckMessage unSubAckMessage = (MqttUnsubAckMessage) MqttMessageFactory.newMessage(
                    new MqttFixedHeader(MqttMessageType.UNSUBACK, false, mqttQoS, false, 0),
                    MqttMessageIdVariableHeader.from(mqttUnsubscribeMessage.variableHeader().messageId()), null);
            channel.writeAndFlush(unSubAckMessage);
        }
    }

    public static void pingReq(Channel channel, MqttMessage msg) {
        MqttMessage pingResp = new MqttMessage(new MqttFixedHeader(MqttMessageType.PINGRESP, false,
                AT_LEAST_ONCE, false, 0));
        channel.writeAndFlush(pingResp);
    }

    public static void disConnect(Channel channel, MqttMessage msg) {
        log.info("The server actively disconnects channelId:{},content：{}", channel.id().asLongText(), msg);
        CHANNEL_CACHE.remove(channel.id().asLongText());
        // 关闭通道
        if (channel.isActive()) {
            channel.close();
        }
    }

    public static void puback(Channel channel, MqttMessage msg) {
        channel.writeAndFlush(msg);
    }

    public static void pubrec(Channel channel, MqttMessage msg) {
        Object variableHeader = msg.variableHeader();
        if (variableHeader instanceof MqttPubReplyMessageVariableHeader header) {
            // qos2类型,接收发布者消息
            MqttMessage mqttMessage = MqttMessageFactory.newMessage(
                    new MqttFixedHeader(MqttMessageType.PUBREL, false,
                            EXACTLY_ONCE, false, 0),
                    MqttMessageIdVariableHeader.from(header.messageId()), 0);
            channel.writeAndFlush(mqttMessage);
        }
    }

    public static void pubrel(Channel channel, MqttMessage msg) {
        Object variableHeader = msg.variableHeader();
        if (variableHeader instanceof MqttPubReplyMessageVariableHeader header) {
            // qos2类型,接收发布者消息
            MqttMessage mqttMessage = MqttMessageFactory.newMessage(
                    new MqttFixedHeader(MqttMessageType.PUBCOMP, false,
                            EXACTLY_ONCE, false, 0),
                    MqttMessageIdVariableHeader.from(header.messageId()), 0);
            channel.writeAndFlush(mqttMessage);
        }
    }

    public static void pubcomp(Channel channel, MqttMessage msg) {
        Object variableHeader = msg.variableHeader();
        if (variableHeader instanceof MqttPubReplyMessageVariableHeader header) {
            // qos2类型,接收发布者消息
            MqttMessage mqttMessage = MqttMessageFactory.newMessage(
                    new MqttFixedHeader(MqttMessageType.PUBCOMP, false,
                            EXACTLY_ONCE, false, 0),
                    MqttMessageIdVariableHeader.from(header.messageId()), 0);
            channel.writeAndFlush(mqttMessage);
        }
    }
}
