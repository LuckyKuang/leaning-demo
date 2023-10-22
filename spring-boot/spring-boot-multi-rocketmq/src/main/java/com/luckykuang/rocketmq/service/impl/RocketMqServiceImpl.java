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

package com.luckykuang.rocketmq.service.impl;

import com.luckykuang.rocketmq.service.RocketMqService;
import com.luckykuang.rocketmq.util.MqUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.luckykuang.rocketmq.constant.RocketMqConstants.producerId;

/**
 * @author luckykuang
 * @date 2023/8/7 15:24
 */
@Slf4j
@Service
public class RocketMqServiceImpl implements RocketMqService {
    @Override
    public void sendTopic1(String msg,String topic,String tags) {
        Message sendMsg = new Message(topic, tags, msg.getBytes(StandardCharsets.UTF_8));
        SendResult sendResult = null;
        try {
            sendResult = MqUtils.getProducer(producerId).send(sendMsg);
        } catch (Exception e) {
            log.error("消息发送异常",e);
            throw new RuntimeException(e);
        }
        log.info("发送消息结果:" + sendResult);
    }

    @Override
    public void sendTopicEntity(String jsonObj,String topic,String tags) {
        Message sendMsg = new Message(topic, tags, jsonObj.getBytes(StandardCharsets.UTF_8));
        SendResult sendResult = null;
        try {
            sendResult = MqUtils.getProducer(producerId).send(sendMsg);
        } catch (Exception e) {
            log.error("消息发送异常",e);
            throw new RuntimeException(e);
        }
        log.info("发送消息结果:" + sendResult);
    }

    @Override
    public void asyncSendTopic1(String msg,String topic,String tags) {
        Message sendMsg = new Message(topic, tags, msg.getBytes(StandardCharsets.UTF_8));
        log.info("发送消息:" + sendMsg);
        try {
            MqUtils.getProducer(producerId).send(sendMsg, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    log.info("发送消息结果:" + sendResult);
                }

                @Override
                public void onException(Throwable e) {
                    log.error("发送消息异常:",e);
                }
            });
        } catch (Exception e){
            log.error("消息发送异常",e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendTopic2(String msg,String topic,String tags) {
        Message sendMsg = new Message(topic, tags, msg.getBytes(StandardCharsets.UTF_8));
        SendResult sendResult = null;
        try {
            sendResult = MqUtils.getProducer(producerId).send(sendMsg);
        } catch (Exception e) {
            log.error("消息发送异常",e);
            throw new RuntimeException(e);
        }
        log.info("发送消息结果:" + sendResult);
    }

    @Override
    public void sendTopic2Batch(String msg,String topic,String tags) {
        List<Message> msgList = new ArrayList<>();
        for (int i = 1; i <= 3000; i++) {
            String msgStr = msg + i;
            Message sendMsg = new Message(topic, tags, msgStr.getBytes(StandardCharsets.UTF_8));
            msgList.add(sendMsg);

            if (i % 5 == 0) {
                SendResult sendResult = null;
                try {
                    sendResult = MqUtils.getProducer(producerId).send(msgList);
                } catch (Exception e) {
                    log.error("消息发送异常",e);
                    throw new RuntimeException(e);
                }
                log.info("发送消息结果:" + sendResult);
                msgList.clear();
            }
        }
    }

    @Override
    public void sendOrderlyBatch() {
        // 开16个线程，分别发生20组订单数据
        for (int i = 0; i < 16; i++) {
            int index = i;
            Thread thread = new Thread(() -> {
                // 模拟20个订单,每个订单隔5S发生一条消息，消息顺序：创建，付款，物流，完成
                for (int j = 0; j < 10; j++) {
                    try {
                        String createMsq = Thread.currentThread().getName() + ",订单编号" + index + "-" + j + ",创建";
                        String createKey = "orderNo" + index + j + "create";
                        Message createSendMsg = new Message("topic3", "order", createKey, createMsq.getBytes(StandardCharsets.UTF_8));
                        log.info(createMsq);
                        SendResult createSendResult = MqUtils.getProducer(producerId).send(createSendMsg, (mqs, message, arg) -> {
                            // 根据订单id选择发送queue
                            Integer id = (Integer) arg;
                            int indexMsg = id % mqs.size();
                            return mqs.get(indexMsg);
                        }, j);
                        log.info("创建订单发送消息结果:" + createSendResult);

                        String paymentMsq = Thread.currentThread().getName() + ",订单编号" + index + "-" + j + ",付款";
                        String paymentKey = "orderNo" + index + j + "payment";
                        Message paymentSendMsg = new Message("topic3", "order", paymentKey, paymentMsq.getBytes(StandardCharsets.UTF_8));
                        log.info(paymentMsq);
                        SendResult paymentSendResult = MqUtils.getProducer(producerId).send(paymentSendMsg, (mqs, message, arg) -> {
                            // 根据订单id选择发送queue
                            Integer id = (Integer) arg;
                            int indexMsg = id % mqs.size();
                            return mqs.get(indexMsg);
                        }, j);
                        log.info("支付订单发送消息结果:" + paymentSendResult);

                        String logisticsMsq = Thread.currentThread().getName() + ",订单编号" + index + "-" + j + ",物流";
                        String logisticsKey = "orderNo" + index + j + "logistics";
                        Message logisticsSendMsg = new Message("topic3", "order", logisticsKey, logisticsMsq.getBytes(StandardCharsets.UTF_8));
                        log.info(logisticsMsq);
                        SendResult logisticsSendResult = MqUtils.getProducer(producerId).send(logisticsSendMsg, (mqs, message, arg) -> {
                            // 根据订单id选择发送queue
                            Integer id = (Integer) arg;
                            int indexMsg = id % mqs.size();
                            return mqs.get(indexMsg);
                        }, j);
                        log.info("发送物流发送消息结果:" + logisticsSendResult);

                        String completeMsq = Thread.currentThread().getName() + ",订单编号" + index + "-" + j + ",完成";
                        String completeKey = "orderNo" + index + j + "logistics";
                        Message completeSendMsg = new Message("topic3", "order", completeKey, completeMsq.getBytes(StandardCharsets.UTF_8));
                        log.info(completeMsq);
                        SendResult completeSendResult = MqUtils.getProducer(producerId).send(completeSendMsg, (mqs, message, arg) -> {
                            // 根据订单id选择发送queue
                            Integer id = (Integer) arg;
                            int indexMsg = id % mqs.size();
                            return mqs.get(indexMsg);
                        }, j);
                        log.info("完成订单发送消息结果:" + completeSendResult);

                    } catch (Exception e) {
                        log.error("发送顺序消息失败，订单号:" + j, e);
                    }
                }
            });
            thread.setName("线程编号" + i);
            thread.start();
        }
    }

    @Override
    public void sendDelayMeg(String msg,String topic,String tags) {
        Message sendMsg = new Message(topic, tags, msg.getBytes(StandardCharsets.UTF_8));
        // 延迟级别对应延迟时间 1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
        // level:0代表无延迟  1  2  3   4   5  6  7  8  9  10 11 12 13 14  15  16  17 18
        sendMsg.setDelayTimeLevel(16);
        SendResult sendResult = null;
        try {
            sendResult = MqUtils.getProducer(producerId).send(sendMsg);
        } catch (Exception e) {
            log.error("消息发送异常",e);
            throw new RuntimeException(e);
        }
        log.info("发送消息结果:" + sendResult);
    }
}
