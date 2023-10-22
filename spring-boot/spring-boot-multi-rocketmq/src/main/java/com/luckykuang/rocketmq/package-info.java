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

/**
 * 注意：同步消息生产者不会处理消息的重复，会根据retryTimesWhenSendFailed和retryTimesWhenSendAsyncFailed的配置数量重复发送消息
 * 想要实现数据的幂等/重试并存，需要开发者自己处理，rocketmq不会去处理消息的幂等性
 * send()方法详细解释 参见 {@link org.apache.rocketmq.client.producer.DefaultMQProducer}
 * 如果需要保证消息不丢失，RocketMQ的broker刷盘策略要配置成同步刷盘(SYNC_FLUSH)
 * @author luckykuang
 * @date 2023/10/22 22:02
 */
package com.luckykuang.rocketmq;