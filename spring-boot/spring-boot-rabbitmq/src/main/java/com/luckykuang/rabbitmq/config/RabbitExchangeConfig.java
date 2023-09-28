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

import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.luckykuang.rabbitmq.constant.RabbitConstants.Exchange.*;


/**
 * 交换机配置类
 * @author luckykuang
 * @date 2023/9/26 15:13
 */
@Configuration
public class RabbitExchangeConfig {

    /**
     * 正常队列交换器
     */
    @Bean
    public Exchange processExchange() {
        return ExchangeBuilder
                // 设置交换机名称
                .directExchange(PROCESS_EXCHANGE)
                // 设置持久标识
                .durable(true)
                .build();
    }

    /**
     * 延时队列交换器(每个消息设置过期时间)
     */
    @Bean
    public Exchange delayExchange() {
        return ExchangeBuilder
                // 设置交换机名称
                .directExchange(DELAY_EXCHANGE)
                // 设置持久标识
                .durable(true)
                .build();
    }

    /**
     * 过期队列交换器(整个队列设置固定过期时间)
     */
    @Bean
    public Exchange delayQueueExchange() {
        return ExchangeBuilder
                // 设置交换机名称
                .directExchange(DELAY_QUEUE_EXCHANGE)
                // 设置持久标识
                .durable(true)
                .build();
    }
}
