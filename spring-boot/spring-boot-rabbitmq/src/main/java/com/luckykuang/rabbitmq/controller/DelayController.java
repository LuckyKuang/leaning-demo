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

package com.luckykuang.rabbitmq.controller;

import com.luckykuang.rabbitmq.service.DelaySendService;
import com.luckykuang.rabbitmq.vo.MessageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 延迟队列，每个消息发送时需要指定消息的过期时间
 * @author luckykuang
 * @date 2023/9/8 15:18
 */
@Tag(name = "延时消息")
@RestController
@RequestMapping(value = "/api/delay")
public class DelayController {

    @Resource
    private DelaySendService delaySendService;

    @Operation(summary = "发送延时消息，每个消息都有自己的过期时间", description = "消息自带过期时间")
    @PostMapping(value = "/sendDelayMessage")
    public String sendDelayMessage(@RequestBody MessageVO messageVO) {
        delaySendService.sendDelayMessage(messageVO);
        return "success";
    }

    @Operation(summary = "发送延时消息队列，整个队列有自己的过期时间", description = "队列整体有固定的过期时间")
    @PostMapping(value = "/sendDelayQueueMessage")
    public String sendDelayQueueMessage(@RequestBody MessageVO messageVO) {
        delaySendService.sendDelayQueueMessage(messageVO);
        return "success";
    }
}
