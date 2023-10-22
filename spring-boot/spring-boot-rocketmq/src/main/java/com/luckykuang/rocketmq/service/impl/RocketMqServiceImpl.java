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

package com.luckykuang.rocketmq.service.impl;

import com.luckykuang.rocketmq.entity.MemberMessage;
import com.luckykuang.rocketmq.service.RocketMqService;
import com.luckykuang.rocketmq.util.RocketMQEnhanceTemplate;
import jakarta.annotation.Resource;
import org.apache.rocketmq.client.producer.SendResult;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

/**
 * @author luckykuang
 * @date 2023/10/22 22:25
 */
@Service
public class RocketMqServiceImpl implements RocketMqService {

    @Resource
    private RocketMQEnhanceTemplate rocketMQEnhanceTemplate;

    private static final String topic = "rocket_enhance";
    private static final String tag = "member";

    /**
     * 发送实体消息
     */
    @Override
    public SendResult member() {
        String key = UUID.randomUUID().toString();
        MemberMessage message = new MemberMessage();
        // 设置业务key
        message.setKey(key);
        // 设置消息来源，便于查询
        message.setSource("MEMBER");
        // 业务消息内容
        message.setUserName("张三");
        message.setBirthday(LocalDate.now());

        return rocketMQEnhanceTemplate.send(topic, tag, message);
    }
}
