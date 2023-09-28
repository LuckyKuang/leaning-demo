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

package com.luckykuang.rabbitmq.service.impl;

import com.google.gson.Gson;
import com.luckykuang.rabbitmq.enums.ErrorCodeEnum;
import com.luckykuang.rabbitmq.exception.SystemException;
import com.luckykuang.rabbitmq.service.DelaySendService;
import com.luckykuang.rabbitmq.util.GsonUtils;
import com.luckykuang.rabbitmq.vo.MessageVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.luckykuang.rabbitmq.constant.RabbitConstants.Exchange.DELAY_EXCHANGE;
import static com.luckykuang.rabbitmq.constant.RabbitConstants.Exchange.DELAY_QUEUE_EXCHANGE;
import static com.luckykuang.rabbitmq.constant.RabbitConstants.Routing.DELAY_ROUTING_KEY;

/**
 * @author luckykuang
 * @date 2023/9/8 15:08
 */
@Slf4j
@Service
public class DelaySendServiceImpl implements DelaySendService {

    private static final Gson GSON = GsonUtils.getGsonInstance();

    @Resource
    private AmqpTemplate rabbitTemplate;

    /**
     * 发送延时消息，每个消息都自己有自己的过期时间
     */
    public void sendDelayMessage(MessageVO msg) {

        // 消息发送时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        log.info("消息发送时间为: {}", sdf.format(new Date()));

        // 设置发送时间，开始发送
        try {
            rabbitTemplate.convertAndSend(DELAY_EXCHANGE, DELAY_ROUTING_KEY, msg,
                    message -> {
                        message.getMessageProperties().setExpiration(String.valueOf(msg.getTtl()));
                        return message;
                    });
        } catch (AmqpException e) {
            log.error("消息发送失败，请检查消息中间件是否正常:{}", GSON.toJson(msg));
            throw new SystemException(ErrorCodeEnum.UNKNOWING_ERROR);
        }
    }

    /**
     * 发送消息，至指定过期时间的队列中。
     * 适用于一些定时任务
     */
    public void sendDelayQueueMessage(MessageVO msg) {

        // 消息发送时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        log.info("消息发送时间为: {}", sdf.format(new Date()));

        // 设置发送时间，开始发送
        try {
            rabbitTemplate.convertAndSend(DELAY_QUEUE_EXCHANGE, DELAY_ROUTING_KEY, msg);
        } catch (AmqpException e) {
            log.error("消息发送失败，请检查消息中间件是否正常:{}", GSON.toJson(msg));
            throw new SystemException(ErrorCodeEnum.UNKNOWING_ERROR);
        }
    }
}
