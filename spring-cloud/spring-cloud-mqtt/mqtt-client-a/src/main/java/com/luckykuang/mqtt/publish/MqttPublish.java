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

package com.luckykuang.mqtt.publish;

import com.luckykuang.mqtt.config.MqttConfig;
import com.luckykuang.mqtt.config.MqttConnect;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.stereotype.Component;

/**
 * 主要实现发布消息和订阅主题，接收消息在回调类PushCallback中
 * 持久化订阅(false)：重连时不需要再次订阅
 * 非持久化订阅(true)：重连时需要再次订阅
 * 参见 {@link MqttConfig} cleanSession
 * @author luckykuang
 * @date 2023/8/9 18:38
 */
@Slf4j
@Component
public class MqttPublish {

    /**
     * 发布者客户端对象
     */
    private MqttClient publishClient;

    /**
     * 订阅者客户端对象
     */
    private MqttClient subscribeClient;

    private final MqttConnect mqttConnect;
    private final MqttConfig config;

    public MqttPublish(MqttConnect mqttConnect, MqttConfig config) {
        this.mqttConnect = mqttConnect;
        this.config = config;
        log.info("publish上线了");
    }

    /**
     * MQTT发送指令：主要是组装消息体
     * @param topic 主题
     * @param data 消息内容
     * @param qos 消息级别
     * level 0：消息最多传递一次，不再关心它有没有发送到对方，也不设置任何重发机制
     * level 1：包含了简单的重发机制，发送消息之后等待接收者的回复，如果没收到回复则重新发送消息。这种模式能保证消息至少能到达一次，但无法保证消息重复
     * level 2：有了重发和重复消息发现机制，保证消息到达对方并且严格只到达一次
     */
    public boolean publishMqttMessage(String topic, String data, int qos) {
        try {
            // 建立连接
            publishConnect();
            MqttTopic mqttTopic = publishClient.getTopic(topic);
            MqttMessage message = new MqttMessage();
            // 消息级别
            message.setQos(qos);
            // true：保留消息，会重复消费
            // false：不保留消息，会发送一条空的消息将之前的消息覆盖
            message.setRetained(config.isCleanSession());
            // 消息内容
            message.setPayload(data.getBytes());
            // 将组装好的消息发出去
            return publish(mqttTopic, message);
        } catch (Exception e) {
            log.error("发布消息异常 topic:{},data:{},qos:{}",topic,data,qos,e);
            throw new IllegalArgumentException("topic:["+topic+"]"+"发布消息异常");
        }
    }

    /**
     * 订阅端订阅消息
     * @param topic 要订阅的主题
     * @param qos 订阅消息的级别
     */
    public boolean subscribeMqttMessage(String topic, int qos) {
        // 建立连接
        subscribeConnect();
        // 以某个消息级别订阅某个主题
        try {
            subscribeClient.subscribe(topic, qos);
            return true;
        } catch (MqttException e) {
            log.error("订阅消息异常 topic:{},qos:{}",topic,qos,e);
            throw new IllegalArgumentException("topic:["+topic+"]"+"订阅消息异常");
        }
    }

    /**
     * 订阅端取消订阅消息
     * @param topic 要订阅的主题
     */
    public boolean unsubscribeMqttMessage(String topic) {
        // 建立连接
        subscribeConnect();
        // 取消订阅某个主题
        try {
            // MQTT协议中订阅关系是持久化的，因此如果不需要订阅某些 Topic，需要调用 unsubscribe 方法取消订阅关系。
            subscribeClient.unsubscribe(topic);
            return true;
        } catch (MqttException e) {
            log.error("取消订阅消息异常 topic:{}",topic,e);
            throw new IllegalArgumentException("topic:["+topic+"]"+"取消订阅消息异常");
        }
    }

    /**
     * 发布时和emqx服务端建立连接
     */
    private void publishConnect() {
        // 防止重复创建MQTTClient实例
        try {
            if (publishClient==null) {
                // 先让客户端和服务器建立连接，MemoryPersistence设置clientId的保存形式，默认为以内存保存
                publishClient = new MqttClient(config.getHost(), config.getClientId(), new MemoryPersistence());
            }
            // 获取mqtt默认配置
            MqttConnectOptions options = mqttConnect.getOptions();
            // 判断拦截状态，这里注意一下，如果没有这个判断，是非常坑的
            if (!publishClient.isConnected()) {
                publishClient.connect(options);
            }
            // 这里的逻辑是如果连接成功就重新连接
            else {
                publishClient.disconnect();
                publishClient.connect(mqttConnect.getOptions(options));
            }
            log.info("---------------------发布端连接成功");
        } catch (MqttException e) {
            log.error("发布端连接异常：",e);
        }
    }

    /**
     * 订阅时和emqx服务端建立连接
     */
    protected void subscribeConnect() {
        try {
            // 防止重复创建MQTTClient实例
            if (subscribeClient ==null) {
                // clientId不能和其它的clientId一样，否则会出现频繁断开连接和重连的问题
                // MemoryPersistence设置clientId的保存形式，默认为以内存保存
                subscribeClient = new MqttClient(config.getHost(), config.getClientId(), new MemoryPersistence());
                // 如果是订阅者则添加回调类，发布不需要，PushCallback类在后面，继续往下看
                subscribeClient.setCallback(new PushCallback(MqttPublish.this));
            }
            // 获取mqtt默认配置
            MqttConnectOptions options = mqttConnect.getOptions();
            // 判断拦截状态，这里注意一下，如果没有这个判断，是非常坑的
            if (!subscribeClient.isConnected()) {
                subscribeClient.connect(options);
            }
            // 这里的逻辑是如果连接成功就重新连接
            else {
                subscribeClient.disconnect();
                subscribeClient.connect(mqttConnect.getOptions(options));
            }
            log.info("---------------------订阅端连接成功");
        } catch (MqttException e) {
            log.error("订阅端连接异常：",e);
        }
    }

    /**
     * 把组装好的消息发出去
     * @param topic 消息主题
     * @param message 消息
     * @return true-发送成功 false-发送失败
     */
    private boolean publish(MqttTopic topic, MqttMessage message) {

        MqttDeliveryToken token = null;
        try {
            // 把消息发送给对应的主题
            token = topic.publish(message);
            token.waitForCompletion();
            // 检查发送是否成功
            boolean flag = token.isComplete();

            StringBuilder sbf = new StringBuilder(200);
            sbf.append("给主题为'");
            sbf.append(topic.getName());
            sbf.append("'发布消息：");
            if (flag) {
                sbf.append("成功！消息内容是：");
                sbf.append(new String(message.getPayload()));
            } else {
                sbf.append("失败！");
            }
            log.info(sbf.toString());
        } catch (MqttException e) {
            log.error("消息发布异常：",e);
        }
        return token != null && token.isComplete();
    }
}
