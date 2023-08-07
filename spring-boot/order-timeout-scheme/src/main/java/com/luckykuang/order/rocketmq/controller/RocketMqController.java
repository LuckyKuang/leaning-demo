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

package com.luckykuang.order.rocketmq.controller;

import com.luckykuang.order.rocketmq.service.RocketMqService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * MQ测试类
 * @author luckykuang
 * @date 2023/8/4 17:53
 */
@Slf4j
@Tag(name = "MQ测试类")
@RestController
@RequestMapping("/rocketmq")
@RequiredArgsConstructor
public class RocketMqController {

    private final RocketMqService rocketMqService;

    @Operation(summary = "发送字符串消息测试到主题Topic1")
    @GetMapping(value = "/sendTopic1")
    public void sendTopic1() throws Exception {
        rocketMqService.sendTopic1();
    }

    @Operation(summary = "发送实体类消息测试到主题Topic1")
    @GetMapping(value = "/sendTopicEntity")
    public void sendTopicEntity() throws Exception {
        rocketMqService.sendTopicEntity();
    }

    @Operation(summary = "异步发送消息测试到主题Topic1")
    @GetMapping(value = "/asyncSendTopic1")
    public void asyncSendTopic1() throws Exception {
        rocketMqService.asyncSendTopic1();
    }

    @Operation(summary = "发送消息测试到主题sendTopic2")
    @GetMapping(value = "/sendTopic2")
    public void sendTopic2() throws Exception {
        rocketMqService.sendTopic2();
    }

    @Operation(summary = "批量发送消息测试")
    @GetMapping(value = "/sendTopic2Batch")
    public void sendTopic2Batch() throws Exception {
        rocketMqService.sendTopic2Batch();
    }

    @Operation(summary = "批量发送有序消息测试")
    @GetMapping(value = "/sendOrderlyBatch")
    public void sendOrderlyBatch() {
        rocketMqService.sendOrderlyBatch();

    }

    @Operation(summary = "发送延迟消息")
    @GetMapping(value = "/sendDelayMeg")
    public void sendDelayMeg() throws Exception {
        rocketMqService.sendDelayMeg();
    }
}
