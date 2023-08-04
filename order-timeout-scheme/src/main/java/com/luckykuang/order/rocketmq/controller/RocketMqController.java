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

package com.luckykuang.order.rocketmq.controller;

import com.luckykuang.order.rocketmq.utils.MqUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * MQ测试类
 * @author luckykuang
 * @date 2023/8/4 17:53
 */
@Slf4j
@Tag(name = "MQ测试类")
@RestController
@RequestMapping("/rocketmq")
public class RocketMqController {

    @Operation(summary = "发送消息测试到主题Topic")
    @GetMapping(value = "/sendTopic1")
    public void sendTopic1() throws Exception {
        String msg = "发生测试消息";
        Message sendMsg = new Message("topic1", "test", msg.getBytes(StandardCharsets.UTF_8));
        SendResult sendResult = Objects.requireNonNull(MqUtils.getProducer("producerTest")).send(sendMsg);
        log.info("发送消息结果:" + sendResult);
    }

    @Operation(summary = "发送消息测试到主题sendTopic2")
    @GetMapping(value = "/sendTopic2")
    public void sendTopic2() throws Exception {
        String msg = "发生测试消息";
        Message sendMsg = new Message("topic2", "test", msg.getBytes(StandardCharsets.UTF_8));

        SendResult sendResult = Objects.requireNonNull(MqUtils.getProducer("producerTest")).send(sendMsg);
        log.info("发送消息结果:" + sendResult);
    }

    @Operation(summary = "批量发送消息测试")
    @GetMapping(value = "/sendTopic2Batch")
    public void sendTopic2Batch() throws Exception {
        List<Message> msgList = new ArrayList<>();
        for (int i = 1; i <= 3000; i++) {
            String msg = "发生测试消息" + i;
            Message sendMsg = new Message("topic2", "test", msg.getBytes(StandardCharsets.UTF_8));
            msgList.add(sendMsg);

            if (i % 5 == 0) {
                SendResult sendResult = Objects.requireNonNull(MqUtils.getProducer("producerTest")).send(msgList);
                log.info("发送消息结果:" + sendResult);
                msgList.clear();
            }
        }
    }

    @Operation(summary = "批量发送有序消息测试")
    @GetMapping(value = "/sendOrderlyBatch")
    public void sendOrderlyBatch() throws Exception {
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
                        // log.info(createMsq);
                        send(createSendMsg, j);


                        String paymentMsq = Thread.currentThread().getName() + ",订单编号" + index + "-" + j + ",付款";
                        String paymentKey = "orderNo" + index + j + "payment";
                        Message paymentSendMsg = new Message("topic3", "order", paymentKey, paymentMsq.getBytes(StandardCharsets.UTF_8));
                        // log.info(paymentMsq);
                        send(paymentSendMsg, j);


                        String logisticsMsq = Thread.currentThread().getName() + ",订单编号" + index + "-" + j + ",物流";
                        String logisticsKey = "orderNo" + index + j + "logistics";
                        Message logisticsSendMsg = new Message("topic3", "order", logisticsKey, logisticsMsq.getBytes(StandardCharsets.UTF_8));
                        // log.info(logisticsMsq);
                        send(logisticsSendMsg, j);


                        String completeMsq = Thread.currentThread().getName() + ",订单编号" + index + "-" + j + ",完成";
                        String completeKey = "orderNo" + index + j + "logistics";
                        Message completeSendMsg = new Message("topic3", "order", completeKey, completeMsq.getBytes(StandardCharsets.UTF_8));
                        // log.info(completeMsq);
                        send(completeSendMsg, j);

                    } catch (Exception e) {
                        log.error("发生顺序消息失败，订单号:" + j, e);
                    }

                }

            });
            thread.setName("线程编号" + i);
            thread.start();
        }

    }

    private void send(Message msg, Integer orderNo) throws InterruptedException, RemotingException, MQClientException, MQBrokerException {
        SendResult sendResult = Objects.requireNonNull(MqUtils.getProducer("producerTest")).send(msg, new MessageQueueSelector() {
            @Override
            public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
                // 根据订单id选择发送queue
                Integer id = (Integer) arg;
                int index = id % mqs.size();
                return mqs.get(index);
            }
        }, orderNo);
    }

    @Operation(summary = "发送延迟消息")
    @GetMapping(value = "/sendDelayMeg")
    public void sendDelayMeg() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String msg = "发生延迟消息,延迟30分钟,当前时间" + sdf.format(new Date());
        Message sendMsg = new Message("topic2", "test", msg.getBytes(StandardCharsets.UTF_8));
        // 延迟级别对应延迟时间 1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
        sendMsg.setDelayTimeLevel(16);
        SendResult sendResult = Objects.requireNonNull(MqUtils.getProducer("producerTest")).send(sendMsg);
        log.info("发送消息结果:" + sendResult);
    }
}
