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

import lombok.Data;

/**
 * 生产者
 * @author luckykuang
 * @date 2023/8/4 16:35
 */
@Data
public class MqProducerVo {
    /**
     * 生产者唯一编号
     */
    private String producerId;

    /**
     * 消息组
     */
    private String groupName;

    /**
     * 注册中心地址
     */
    private String nameServer;

    /**
     * 消息最大长度
     */
    private Integer maxMessageSize;

    /**
     * 发送消息超时时间
     */
    private Integer sendMessageTimeOut;

    /**
     * 同步发送消息失败重试次数
     */
    private Integer retryTimesWhenSendFailed;

    /**
     * 异步发送消息失败重试次数
     */
    private Integer retryTimesWhenSendAsyncFailed;
}
