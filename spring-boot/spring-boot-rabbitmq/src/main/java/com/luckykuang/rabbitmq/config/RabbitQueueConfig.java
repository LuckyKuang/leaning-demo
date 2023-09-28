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

package com.luckykuang.rabbitmq.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.luckykuang.rabbitmq.constant.RabbitConstants.Exchange.PROCESS_EXCHANGE;
import static com.luckykuang.rabbitmq.constant.RabbitConstants.Queue.*;
import static com.luckykuang.rabbitmq.constant.RabbitConstants.Routing.DELAY_ROUTING_KEY;


/**
 * 队列配置类
 * @author luckykuang
 * @date 2023/9/26 15:13
 */
@Configuration
public class RabbitQueueConfig {

    /**
     * 正常队列
     * 每个消息过期了都会自动路由到该队列绑定的交换器上，普通队列声明只需要设置队列名称以及是否持久化等信息
     */
    @Bean
    public Queue processQueue() {
        return QueueBuilder.durable(PROCESS_QUEUE)
                .build();
    }

    /**
     * 延迟队列
     * 每个消息过期了都会自动发送给withArgument指定的exchange和指定的routing-key
     */
    @Bean
    public Queue delayQueue() {
        return QueueBuilder.durable(DELAY_QUEUE_MSG)
                // 延迟队列需要设置的消息过期后会发往的交换器名称
                .withArgument("x-dead-letter-exchange", PROCESS_EXCHANGE)
                // 延迟队列需要设置的消息过期后会发往的路由键名称
                .withArgument("x-dead-letter-routing-key", DELAY_ROUTING_KEY)
                .build();
    }

    /**
     * 过期队列
     * 指拥有固定过期时间的队列，其中的消息，每过30秒过期一次，全部转入到指定的x-dead-letter-xx 参数指定的交换器和路由键的队列中。
     */
    @Bean
    public Queue delayQueueQueue() {
        return QueueBuilder.durable(DELAY_QUEUE_QUEUE)
                .withArgument("x-dead-letter-exchange", PROCESS_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DELAY_ROUTING_KEY)
                .withArgument("x-message-ttl", 30000)
                .build();
    }
}
