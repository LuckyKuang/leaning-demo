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

package com.luckykuang.rocketmq.config;

import com.luckykuang.rocketmq.consumer.MqConsumerService;
import com.luckykuang.rocketmq.util.ApplicationContextUtils;
import com.luckykuang.rocketmq.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RocketMq 初始化
 * 监听事件发布类型：
 *      ApplicationContextInitializedEvent：在SpringApplication启动、ApplicationContext准备好、applicationcontextinitializer被调用时发布，但在加载任何bean定义之前。
 *      ApplicationEnvironmentPreparedEvent：当SpringApplication启动并且环境首次可用于检查和修改时发布。
 *      ApplicationFailedEvent：当SpringApplication启动失败时发布。
 *      ApplicationPreparedEvent：当SpringApplication启动时，ApplicationContext已经完全准备好，但没有刷新。在此阶段，将加载bean定义并准备好使用环境。
 *      ApplicationReadyEvent：尽可能晚地发布事件，以表明应用程序已准备好为请求提供服务。事件的源是SpringApplication本身，但要注意不要修改其内部状态，因为到那时所有初始化步骤都已完成。
 *      ApplicationStartedEvent：在刷新应用程序上下文之后，但在调用任何应用程序和命令行运行程序之前发布的事件。
 *      ApplicationStartingEvent：事件尽可能早地发布——在SpringApplication启动之后，在Environment或ApplicationContext可用之前，但在ApplicationListeners注册之后。事件的源是SpringApplication本身，但是要注意在早期阶段不要过多地使用它的内部状态，因为它可能在生命周期的后期被修改。
 *          参见：{@link org.springframework.boot.context.event.SpringApplicationEvent}
 * @author luckykuang
 * @date 2023/8/4 17:27
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RocketMqInit implements ApplicationListener<ApplicationReadyEvent> {

    /**
     * 获取配置
     */
    private final RocketMqConfig rocketMqConfig;

    /**
     * 存放所有生产者
     */
    public static final Map<String, DefaultMQProducer> PRODUCER_CACHE = new ConcurrentHashMap<>();

//    private static final List<DefaultMQPushConsumer> startConsumer = new ArrayList<>();

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        initProducer();
        initConsumer();
    }

    /**
     * 生产者初始化
     */
    private void initProducer() {
        log.info("正在创建生产者---------------------------------------");
        List<MqProducerVo> producers = rocketMqConfig.getProducers();
        if (CollectionUtils.isEmpty(producers)) {
            log.info("没有配置MQ生产者---------------------------------------");
        }
        producers.forEach(producer -> {
            try {
                DefaultMQProducer defaultMQProducer = new DefaultMQProducer(producer.getGroupName());
                defaultMQProducer.setNamesrvAddr(producer.getNameServer());
                defaultMQProducer.setVipChannelEnabled(false);
                defaultMQProducer.setMaxMessageSize(producer.getMaxMessageSize());
                defaultMQProducer.setSendMsgTimeout(producer.getSendMessageTimeOut());
                defaultMQProducer.setRetryTimesWhenSendFailed(producer.getRetryTimesWhenSendFailed());
                defaultMQProducer.setRetryTimesWhenSendAsyncFailed(producer.getRetryTimesWhenSendFailed());
                defaultMQProducer.start();
                PRODUCER_CACHE.put(producer.getProducerId(), defaultMQProducer);
                log.info("mq生产者{},{}启动成功", producer.getGroupName(), producer.getNameServer());
            } catch (MQClientException e) {
                log.error("mq生产者{},{}启动失败", producer.getGroupName(), producer.getNameServer(), e);
            }
        });
    }

    /**
     * 消费者初始化
     * 消费模型：
     *    CLUSTERING：集群
     *    BROADCASTING：广播
     *      参见：{@link MessageModel}
     * 消费者启动时的消费点。有三个消耗点：
     *    CONSUME_FROM_LAST_OFFSET:消费者客户端从先前停止的位置拾取。如果它是一个新启动的消费者客户端，根据消费者群体的年龄，有两种情况:
     *        1.如果该消费群体创建时间较晚，所订阅的最早的消息还没有过期，即该消费群体代表的是最近推出的业务，则消费将从头开始;
     *        2.如果订阅的最早消息已经过期，则消费将从最新消息开始，这意味着在引导时间戳之前生成的消息将被忽略。
     *    CONSUMER_FROM_FIRST_OFFSET:消费者客户端将从最早可用的消息开始。
     *    CONSUME_FROM_TIMESTAMP:消费者客户端将从指定的时间戳开始，这意味着在consumeTimestamp之前出生的消息将被忽略
     *      参见：{@link ConsumeFromWhere}
     * 顺序消费状态
     *      参见：{@link ConsumeOrderlyStatus}
     * 无序消费状态
     *      参见：{@link ConsumeConcurrentlyStatus}
     */
    private void initConsumer() {
        log.info("正在创建消费者---------------------------------------");
        List<MqConsumerVo> consumers = rocketMqConfig.getConsumers();
        // 初始化消费者
        if (CollectionUtils.isEmpty(consumers)) {
            log.info("没有配置MQ消费者---------------------------------------");
            return;
        }

        consumers.forEach(consumer -> {
            DefaultMQPushConsumer defaultMQPushConsumer = new DefaultMQPushConsumer(consumer.getGroupName());
            defaultMQPushConsumer.setInstanceName(UUID.randomUUID().toString());
            defaultMQPushConsumer.setNamesrvAddr(consumer.getNameServer());
            defaultMQPushConsumer.setConsumeThreadMin(consumer.getConsumeThreadMin());
            defaultMQPushConsumer.setConsumeThreadMax(consumer.getConsumeThreadMax());
            defaultMQPushConsumer.setConsumeMessageBatchMaxSize(consumer.getConsumeMessageBatchMaxSize());

            // 设置消费模型，默认为集群
            String messageModel = consumer.getMessageModel();
            if (StringUtils.isBlank(messageModel) || StringUtils.equals("CLUSTERING", messageModel)) {
                defaultMQPushConsumer.setMessageModel(MessageModel.CLUSTERING);
            } else {
                defaultMQPushConsumer.setMessageModel(MessageModel.BROADCASTING);
            }

            // 设置消费者启动时的消费点
            defaultMQPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);

            // 设置监听,判断是否为顺序消费
            Boolean orderly = consumer.getOrderly();
            if (orderly == null || orderly) {
                // 顺序消费
                setConsumeOrderly(defaultMQPushConsumer);
            } else {
                // 无序消费
                setConsumeConcurrently(defaultMQPushConsumer);
            }

            List<MqTopicVo> topics = consumer.getTopics();
            if (CollectionUtils.isEmpty(topics)) {
                log.info("没有配置MQ消费者主题,启动失败---------------------------------------");
                return;
            }

            try {
                for (MqTopicVo topic : topics) {
                    // 消费者订阅主题，标签默认为"*"
                    defaultMQPushConsumer.subscribe(topic.getTopicName(), StringUtils.isBlank(topic.getTagName()) ? "*" : topic.getTagName());
                }
                defaultMQPushConsumer.start();
//                startConsumer.add(defaultMQPushConsumer);
                log.info("MQ消费者group:{},nameServer:{}启动成功", consumer.getGroupName(), consumer.getNameServer());
            } catch (MQClientException e) {
                log.error("MQ消费者group:{},nameServer:{},启动失败",consumer.getGroupName(), consumer.getNameServer(), e);
            }
        });
    }

    /**
     * 无序消费
     */
    private static void setConsumeConcurrently(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.registerMessageListener((MessageListenerConcurrently) (list, consumeConcurrentlyContext) -> {

            if (CollectionUtils.isEmpty(list)) {
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }

            // MQ不会一次拉取多个不同Topic消息,直接取第一个
            String topicName = list.get(0).getTopic();

            // 获取对应实际处理类
            MqConsumerService mqConsumerService = ApplicationContextUtils.getBean("consumer." + topicName, MqConsumerService.class);

            if (mqConsumerService == null) {
                log.info("根据topic:{}未找到对应处理类,请检查代码", topicName);
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
        });
    }

    /**
     * 顺序消费
     */
    private static void setConsumeOrderly(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.registerMessageListener((MessageListenerOrderly) (list, consumeOrderlyContext) -> {
            if (CollectionUtils.isEmpty(list)) {
                // 成功
                return ConsumeOrderlyStatus.SUCCESS;
            }
            // MQ不会一次拉取多个不同Topic消息,直接取第一个
            String topicName = list.get(0).getTopic();

            // 获取对应实际处理类
            MqConsumerService mqConsumerService = ApplicationContextUtils.getBean("consumer." + topicName, MqConsumerService.class);

            if (mqConsumerService == null) {
                log.info("根据topic:{}未找到对应处理类,请检查代码", topicName);
                // 暂时挂起当前队列
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
        });
    }
}
