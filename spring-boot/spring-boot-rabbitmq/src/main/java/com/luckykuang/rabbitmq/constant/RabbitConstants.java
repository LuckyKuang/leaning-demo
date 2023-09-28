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

package com.luckykuang.rabbitmq.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 常量配置
 * @author luckykuang
 * @date 2023/9/26 15:22
 */
public interface RabbitConstants {

    /**
     * 声明交换器
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    final class Exchange {
        /**
         * 正常交换器
         */
        public static final String PROCESS_EXCHANGE = "process_exchange";

        /**
         * 延时交换器
         */
        public static final String DELAY_EXCHANGE = "delay_exchange";

        /**
         * 过期交换器
         */
        public static final String DELAY_QUEUE_EXCHANGE = "delay_queue_exchange";
    }

    /**
     * 声明队列
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    final class Queue {
        /**
         * 正常消费过期消息的队列
         * 只有该队列有消费者
         */
        public static final String PROCESS_QUEUE = "process_queue";

        /**
         * 延时消费队列
         * 该队列没有直接消费者
         * 消息过期后进入到process queue队列中，每个消息都可以自定义自己的过期时间
         */
        public static final String DELAY_QUEUE_MSG = "delay_queue";

        /**
         * 过期消费队列
         * 有固定过期时间的队列，队列中的所有消息都将过期
         */
        public static final String DELAY_QUEUE_QUEUE = "delay_queue_queue";
    }

    /**
     * 声明路由键
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    final class Routing {
        /**
         * 延时消息路由键
         */
        public static final String DELAY_ROUTING_KEY = "delay";
    }
}
