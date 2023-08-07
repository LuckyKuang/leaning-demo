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

package com.luckykuang.order.rocketmq.config;

import com.luckykuang.order.rocketmq.consumer.MqConsumerService;
import com.luckykuang.order.rocketmq.utils.ApplicationContextUtils;
import com.luckykuang.order.rocketmq.vo.MqConsumerParam;
import com.luckykuang.order.rocketmq.vo.MqConsumerResult;
import com.luckykuang.order.rocketmq.vo.MqConsumerVo;
import com.luckykuang.order.rocketmq.vo.MqTopicVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.*;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 消费者初始化
 * @author luckykuang
 * @date 2023/8/4 16:58
 */
@Slf4j
@Order(10)
@Component
@RequiredArgsConstructor
public class InitMqConsumer implements ApplicationListener<ApplicationReadyEvent> {

    /**
     * 获取消费者配置
     */
    private final MqConsumerConfig mqConsumerConfig;

    private final List<DefaultMQPushConsumer> startConsumer = new ArrayList<>();

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("正在创建消费者---------------------------------------");
        List<MqConsumerVo> consumerlist = mqConsumerConfig.getConsumers();
        // 初始化消费者
        if (CollectionUtils.isEmpty(consumerlist)) {
            log.info("无MQ消费者---------------------------------------");
        }

        consumerlist.forEach(consumer -> {
            // 此步可以针对配置参数进行校验，校验consumer格式，符合规则才启动--待完善

            DefaultMQPushConsumer start = new DefaultMQPushConsumer(consumer.getGroupName());
            start.setInstanceName(UUID.randomUUID().toString());
            start.setNamesrvAddr(consumer.getNameServer());
            start.setConsumeThreadMin(consumer.getConsumeThreadMin());
            start.setConsumeThreadMax(consumer.getConsumeThreadMax());
            start.setConsumeMessageBatchMaxSize(consumer.getConsumeMessageBatchMaxSize());
            // 设置消费模型，集群(MessageModel.CLUSTERING)还是广播(MessageModel.BROADCASTING)，默认为集群
            String messageModel = consumer.getMessageModel();
            if (StringUtils.isBlank(messageModel) || StringUtils.equals("CLUSTERING", messageModel)) {
                start.setMessageModel(MessageModel.CLUSTERING);
            } else {
                start.setMessageModel(MessageModel.BROADCASTING);
            }
            // 设置consumer第一次启动是从队列头部开始还是队列尾部开始,否则按照上次消费的位置继续消费
            start.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);

            // 设置监听,判断是否为顺序消费
            Boolean orderly = consumer.getOrderly();
            if (orderly == null || orderly) {
                // 顺序消费
                start.registerMessageListener(new MessageListenerOrderly() {
                    @Override
                    public ConsumeOrderlyStatus consumeMessage(List<MessageExt> list, ConsumeOrderlyContext consumeOrderlyContext) {
                        if (CollectionUtils.isEmpty(list)) {
                            return ConsumeOrderlyStatus.SUCCESS;
                        }
                        // MQ不会一次拉取多个不同Topic消息,直接取第一个
                        String topicName = list.get(0).getTopic();

                        // 获取对应实际处理类
                        MqConsumerService mqConsumerService = ApplicationContextUtils.getBean("mqConsumerService." + topicName, MqConsumerService.class);

                        if (mqConsumerService == null) {
                            log.info("未根据topic:{}找到对应处理类,请检查代码", topicName);
                            return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
                        }

                        MqConsumerResult result = mqConsumerService.handle(new MqConsumerParam(list,null,consumeOrderlyContext));

                        if (result.isSaveConsumeLog()) {
                            // 判断是否需要记录日志,落库或者缓存
                        }

                        // 判断是否成功
                        if (result.isSuccess()) {
                            return ConsumeOrderlyStatus.SUCCESS;
                        } else {
                            // 失败是否需要重试
                            if (result.isReconsumeLater()) {
                                // 有序消费,最好在业务消费类中加入消费次数记录，当消费达到多少次之后，还是失败则返回成功，并且加入日志加预警功能
                                // 因为有序消费返回SUSPEND_CURRENT_QUEUE_A_MOMENT会一直消费，导致其他消息处理不了
                                return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
                            } else {
                                return ConsumeOrderlyStatus.SUCCESS;
                            }
                        }
                    }
                });
            } else {
                start.registerMessageListener(new MessageListenerConcurrently() {
                    @Override
                    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {

                        if (CollectionUtils.isEmpty(list)) {
                            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                        }
                        // MQ不会一次拉取多个不同Topic消息,直接取第一个
                        String topicName = list.get(0).getTopic();

                        // 获取对应实际处理类
                        MqConsumerService mqConsumerService = ApplicationContextUtils.getBean("mqConsumerService." + topicName, MqConsumerService.class);

                        if (mqConsumerService == null) {
                            log.info("未根据topic:{}找到对应处理类,请检查代码", topicName);
                            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                        }

                        MqConsumerResult result = mqConsumerService.handle(new MqConsumerParam(list,consumeConcurrentlyContext,null));

                        if (result.isSaveConsumeLog()) {
                            // 判断是否需要记录日志,落库或者缓存--待完善
                        }

                        // 判断是否成功
                        if (result.isSuccess()) {
                            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                        } else {
                            // 失败是否需要重试,默认失败次数达到16次消息会进入死信队列
                            if (result.isReconsumeLater()) {
                                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                            } else {
                                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                            }
                        }
                    }
                });
            }

            List<MqTopicVo> topics = consumer.getTopics();
            if (CollectionUtils.isEmpty(topics)) {
                // 未配置主题，则不启动
                return;
            }

            try {
                for (MqTopicVo topic : topics) {
                    start.subscribe(topic.getTopicName(), StringUtils.isBlank(topic.getTagName()) ? "*" : topic.getTagName());
                }
                start.start();
                startConsumer.add(start);
                log.info("MQ消费者group:{},namesrv:{}启动成功", consumer.getGroupName(), consumer.getNameServer());
            } catch (MQClientException e) {
                log.error("MQ消费者group:" + consumer.getGroupName() + ",namesrv:" + consumer.getNameServer() + "启动失败", e);
            }

        });
    }
}
