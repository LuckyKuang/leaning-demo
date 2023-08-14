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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * 消息回调
 * connectionLost() 连接断开回调方法：实现于 MqttCallback 接口，在客户端连接断开时触发
 * messageArrived() 消息送达回调方法：实现于 MqttCallback 接口，在客户端收到订阅的消息时触发
 * deliveryComplete() 传送成功回调方法：实现于 MqttCallback 接口，在客户端发送消息至服务器成功时触发
 * connectComplete() 连接成功回调方法：实现于 MqttCallbackExtended接口，MqttCallbackExtended接口继承 MqttCallback 接口，在客户端与服务器连接成功时触发
 * @author luckykuang
 * @date 2023/8/9 17:48
 */
@Slf4j
@RequiredArgsConstructor
public class PushCallback implements MqttCallbackExtended {

    private final MqttPublish mqttPublish;

    /**
     * 连接丢失后重连
     * @param throwable 连接丢失抛出异常信息
     */
    @Override
    public void connectionLost(Throwable throwable) {
        // 连接丢失后，一般在这里面进行重连
        log.warn("---------------------mqtt连接断开，尝试重连：{}",throwable.getMessage());
        while (true) {
            try {
                TimeUnit.SECONDS.sleep(30);
                mqttPublish.subscribeConnect();
                log.info("---------------------mqtt尝试重连成功");
                break;
            }catch (Exception e){
                log.error("mqtt连接重试异常：",e);
            }
        }
    }

    /**
     * 接收所订阅的主题的消息并处理
     * @param topic 消息主题
     * @param mqttMessage 消息内容
     * @throws Exception 异常信息
     */
    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        // 订阅后得到的消息会执行到这里面
        String result = new String(mqttMessage.getPayload(), StandardCharsets.UTF_8);
        log.info("接收消息主题：" + topic);
        log.info("接收消息ID：" + mqttMessage.getId());
        log.info("接收消息Qos：" + mqttMessage.getQos());
        log.info("接收消息内容：" + result);
        // 这里可以针对收到的消息做处理，比如持久化
    }

    /**
     * 发送消息，消息到达后处理方法
     * @param token 消息回调
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        log.info("---------------------交付完成 complete:{},messageId:{}",token.isComplete(),token.getMessageId());
    }

    /**
     * 客户端与服务器连接成功时触发
     * @param reconnect If true, the connection was the result of automatic reconnect.
     * @param serverURI The server URI that the connection was made to.
     */
    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        log.info("---------------------连接完成 reconnect:{},serverURI:{}",reconnect,serverURI);
    }
}
