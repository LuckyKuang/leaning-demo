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

package com.luckykuang.rabbitmq.receives;

import com.google.gson.Gson;
import com.luckykuang.rabbitmq.util.GsonUtils;
import com.luckykuang.rabbitmq.vo.CreateOrderVO;
import com.luckykuang.rabbitmq.vo.MessageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author luckykuang
 * @date 2023/9/8 14:13
 */
@Slf4j
@Component
@RabbitListener(queues = "process_queue")
public class OrderDelayConsumer {

    private static final Gson GSON = GsonUtils.getGsonInstance();

    @RabbitHandler
    public void orderConsumer(MessageVO messageVO){

        // 接收到处理队列中的消息的，就是指定时间过期的消息
        // 这里处理每一条消息中的订单编号，去查询对应的订单支付状态，如果处于未支付状态，就取消用户的订单
        try {
            CreateOrderVO orderVo = GSON.fromJson(messageVO.getContent(), CreateOrderVO.class);
            // 获取订单编号，去查询对应的支付结果
            log.info("订单编号为: {}", orderVo.getOrderNo());
        } catch (Exception e) {
            log.error("订单消息解析异常，请检查消息格式是否正确:{}", messageVO.getContent());
        }
    }
}
