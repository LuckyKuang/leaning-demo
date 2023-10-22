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

import com.luckykuang.rocketmq.vo.MqConsumerVo;
import com.luckykuang.rocketmq.vo.MqProducerVo;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * RocketMq 配置
 * @author luckykuang
 * @date 2023/8/4 16:45
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "rocketmq")
public class RocketMqConfig {
    /**
     * 生产者订阅配置
     */
    private List<MqProducerVo> producers;
    /**
     * 消费者订阅配置
     */
    private List<MqConsumerVo> consumers;
}
