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

package com.luckykuang.rocketmq.consumer;


import com.luckykuang.rocketmq.vo.MqConsumerParam;
import com.luckykuang.rocketmq.vo.MqConsumerResult;

/**
 * 消费者公共接口
 * @author luckykuang
 * @date 2023/8/4 17:08
 */
public interface MqConsumerService {
    /**
     * 实际消费者继承该类
     * @param param
     * @return
     */
    MqConsumerResult handle(MqConsumerParam param);
}
