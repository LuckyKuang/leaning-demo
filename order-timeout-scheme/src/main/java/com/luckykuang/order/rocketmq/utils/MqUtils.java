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

package com.luckykuang.order.rocketmq.utils;

import com.luckykuang.order.rocketmq.config.InitMqProducer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.util.CollectionUtils;

/**
 * 消息生产者获取工具类
 * @author luckykuang
 * @date 2023/8/4 17:48
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MqUtils {
    /**
     * 根据生产者唯一标识获取发送实例
     * @param producerId 生产者唯一标识
     * @return 参见 {@link DefaultMQProducer}
     */
    public static DefaultMQProducer getProducer(String producerId) {
        if (CollectionUtils.isEmpty(InitMqProducer.producerMap)) {
            return null;
        } else {
            return InitMqProducer.producerMap.get(producerId);
        }
    }
}
