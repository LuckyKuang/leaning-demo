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

package com.luckykuang.rocketmq.controller;

import com.alibaba.fastjson.JSON;
import com.luckykuang.rocketmq.service.RocketMqService;
import com.luckykuang.rocketmq.vo.UserVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;

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
    public void sendTopic1() {
        String msg = "发送测试消息";
        String topic = "topic1";
        String tags = "test";
        rocketMqService.sendTopic1(msg,topic,tags);
    }

    @Operation(summary = "发送实体类消息测试到主题Topic1")
    @GetMapping(value = "/sendTopicEntity")
    public void sendTopicEntity() {
        UserVo userVo = UserVo.getUser();
        String jsonObj = JSON.toJSONString(userVo);
        String topic = "topic1";
        String tags = "test";
        rocketMqService.sendTopicEntity(jsonObj,topic,tags);
    }

    @Operation(summary = "异步发送消息测试到主题Topic1")
    @GetMapping(value = "/asyncSendTopic1")
    public void asyncSendTopic1() {
        String msg = "异步发送测试消息";
        String topic = "topic1";
        String tags = "test";
        rocketMqService.asyncSendTopic1(msg,topic,tags);
    }

    @Operation(summary = "发送消息测试到主题sendTopic2")
    @GetMapping(value = "/sendTopic2")
    public void sendTopic2() {
        String msg = "发送测试消息";
        String topic = "topic2";
        String tags = "test";
        rocketMqService.sendTopic2(msg,topic,tags);
    }

    @Operation(summary = "批量发送消息测试")
    @GetMapping(value = "/sendTopic2Batch")
    public void sendTopic2Batch() {
        String msg = "发送测试消息";
        String topic = "topic2";
        String tags = "test";
        rocketMqService.sendTopic2Batch(msg,topic,tags);
    }

    @Operation(summary = "批量发送有序消息测试")
    @GetMapping(value = "/sendOrderlyBatch")
    public void sendOrderlyBatch() {
        rocketMqService.sendOrderlyBatch();

    }

    @Operation(summary = "发送延迟消息")
    @GetMapping(value = "/sendDelayMeg")
    public void sendDelayMeg() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String msg = "发生延迟消息,延迟30分钟,当前时间" + sdf.format(new Date());
        String topic = "topic2";
        String tags = "test";
        rocketMqService.sendDelayMeg(msg,topic,tags);
    }
}
