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

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.luckykuang.rabbitmq.constant.RabbitConstants.Routing.DELAY_ROUTING_KEY;


/**
 * 路由配置类
 * 当队列声明完成，交换器声明完成后，就需要进行二者的绑定，并制定路由键绑定的路由规则
 * @author luckykuang
 * @date 2023/9/26 15:14
 */
@Configuration
@RequiredArgsConstructor
public class RabbitRoutingConfig {

    private final RabbitExchangeConfig exchangeConfig;
    private final RabbitQueueConfig queueConfig;

    /**
     * 正常队列绑定到正常交换器，并制定路由键为 DELAY_ROUTING_KEY
     * 谁绑定到谁，并指定路由键
     */
    @Bean
    public Binding processBiding() {
        return BindingBuilder.bind(queueConfig.processQueue())
                .to(exchangeConfig.processExchange())
                .with(DELAY_ROUTING_KEY)
                .noargs();
    }

    /**
     * 延迟队列绑定到延迟交换器，并制定路由键为 DELAY_ROUTING_KEY
     * 谁绑定到谁，并指定路由键
     */
    @Bean
    public Binding delayBiding() {
        return BindingBuilder.bind(queueConfig.delayQueue())
                .to(exchangeConfig.delayExchange())
                .with(DELAY_ROUTING_KEY)
                .noargs();
    }

    /**
     * 过期队列绑定到过期队列交换器，并指定路由键
     */
    @Bean
    public Binding delayQueueBinding() {
        return BindingBuilder.bind(queueConfig.delayQueueQueue())
                .to(exchangeConfig.delayQueueExchange())
                .with(DELAY_ROUTING_KEY)
                .noargs();
    }
}
