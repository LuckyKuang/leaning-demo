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

package com.luckykuang.rocketmq.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

/**
 * @author luckykuang
 * @date 2023/8/4 17:08
 */
@Data
@AllArgsConstructor
public class MqConsumerParam {
    /**
     * 消息
     */
    private List<MessageExt> list;

    /**
     * 非顺序消费上下文
     */
    private ConsumeConcurrentlyContext consumeConcurrentlyContext;

    /**
     * 顺序消费上下文
     */
    private ConsumeOrderlyContext consumeOrderlyContext;
}
