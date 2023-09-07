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

package com.luckykuang.order.rocketmq.vo;

import lombok.Data;

import java.util.List;

/**
 * 消费者
 * @author luckykuang
 * @date 2023/8/4 16:40
 */
@Data
public class MqConsumerVo {
    /**
     * 消息组
     */
    private String groupName;

    /**
     * 注册中心地址
     */
    private String nameServer;

    /**
     * 监听集合
     */
    private List<MqTopicVo> topics;

    /**
     * 消费者最小线程数
     */
    private Integer consumeThreadMin = 2;

    /**
     * 消费者最大线程数
     */
    private Integer consumeThreadMax = 4;

    /**
     * 设置一次消费的条数，默认1
     */
    private Integer consumeMessageBatchMaxSize = 1;

    /**
     * 是否顺序消费
     */
    private Boolean orderly;

    /**
     * 消费模型 BROADCASTING-广播 CLUSTERING-聚类
     * 参见 {@link org.apache.rocketmq.common.protocol.heartbeat.MessageModel}
     */
    private String messageModel;
}
