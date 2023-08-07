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

package com.luckykuang.order.rocketmq.service.impl;

import com.alibaba.fastjson.JSON;
import com.luckykuang.order.rocketmq.processor.RocketMqServiceProcessor;
import com.luckykuang.order.rocketmq.service.RocketMqService;
import com.luckykuang.order.rocketmq.utils.MqUtils;
import com.luckykuang.order.rocketmq.vo.UserVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author luckykuang
 * @date 2023/8/7 15:24
 */
@Slf4j
@Service
public class RocketMqServiceImpl implements RocketMqService {
    @Override
    public void sendTopic1() throws MQBrokerException, RemotingException, InterruptedException, MQClientException {
        String msg = "发生测试消息";
        Message sendMsg = new Message("topic1", "test", msg.getBytes(StandardCharsets.UTF_8));
        SendResult sendResult = Objects.requireNonNull(MqUtils.getProducer("producerTest")).send(sendMsg);
        log.info("发送消息结果:" + sendResult);
    }

    @Override
    public void sendTopicEntity() throws MQBrokerException, RemotingException, InterruptedException, MQClientException {
        UserVo userVo = UserVo.getUser();
        Message sendMsg = new Message("topic1", "test", JSON.toJSONString(userVo).getBytes(StandardCharsets.UTF_8));
        SendResult sendResult = Objects.requireNonNull(MqUtils.getProducer("producerTest")).send(sendMsg);
        log.info("发送消息结果:" + sendResult);
    }

    @Override
    public void asyncSendTopic1() throws RemotingException, InterruptedException, MQClientException {
        String msg = "异步发生测试消息";
        Message sendMsg = new Message("topic1", "test", msg.getBytes(StandardCharsets.UTF_8));
        log.info("发送消息:" + sendMsg);
        Objects.requireNonNull(MqUtils.getProducer("producerTest")).send(sendMsg, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("发送消息结果:" + sendResult);
            }

            @Override
            public void onException(Throwable e) {
                log.error("发送消息异常:",e);
            }
        });
    }

    @Override
    public void sendTopic2() throws MQBrokerException, RemotingException, InterruptedException, MQClientException {
        String msg = "发生测试消息";
        Message sendMsg = new Message("topic2", "test", msg.getBytes(StandardCharsets.UTF_8));
        SendResult sendResult = Objects.requireNonNull(MqUtils.getProducer("producerTest")).send(sendMsg);
        log.info("发送消息结果:" + sendResult);
    }

    @Override
    public void sendTopic2Batch() throws MQBrokerException, RemotingException, InterruptedException, MQClientException {
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
                        RocketMqServiceProcessor.send(createSendMsg, j);

                        String paymentMsq = Thread.currentThread().getName() + ",订单编号" + index + "-" + j + ",付款";
                        String paymentKey = "orderNo" + index + j + "payment";
                        Message paymentSendMsg = new Message("topic3", "order", paymentKey, paymentMsq.getBytes(StandardCharsets.UTF_8));
                        log.info(paymentMsq);
                        RocketMqServiceProcessor.send(paymentSendMsg, j);

                        String logisticsMsq = Thread.currentThread().getName() + ",订单编号" + index + "-" + j + ",物流";
                        String logisticsKey = "orderNo" + index + j + "logistics";
                        Message logisticsSendMsg = new Message("topic3", "order", logisticsKey, logisticsMsq.getBytes(StandardCharsets.UTF_8));
                        log.info(logisticsMsq);
                        RocketMqServiceProcessor.send(logisticsSendMsg, j);

                        String completeMsq = Thread.currentThread().getName() + ",订单编号" + index + "-" + j + ",完成";
                        String completeKey = "orderNo" + index + j + "logistics";
                        Message completeSendMsg = new Message("topic3", "order", completeKey, completeMsq.getBytes(StandardCharsets.UTF_8));
                        log.info(completeMsq);
                        RocketMqServiceProcessor.send(completeSendMsg, j);

                    } catch (Exception e) {
                        log.error("发生顺序消息失败，订单号:" + j, e);
                    }
                }
            });
            thread.setName("线程编号" + i);
            thread.start();
        }
    }

    @Override
    public void sendDelayMeg() throws MQBrokerException, RemotingException, InterruptedException, MQClientException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String msg = "发生延迟消息,延迟30分钟,当前时间" + sdf.format(new Date());
        Message sendMsg = new Message("topic2", "test", msg.getBytes(StandardCharsets.UTF_8));
        // 延迟级别对应延迟时间 1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
        // level:0代表无延迟  1  2  3   4   5  6  7  8  9  10 11 12 13 14  15  16  17 18
        sendMsg.setDelayTimeLevel(16);
        SendResult sendResult = Objects.requireNonNull(MqUtils.getProducer("producerTest")).send(sendMsg);
        log.info("发送消息结果:" + sendResult);
    }
}
