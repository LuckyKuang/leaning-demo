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
import com.luckykuang.rabbitmq.service.DelaySendService;
import com.luckykuang.rabbitmq.service.OrderService;
import com.luckykuang.rabbitmq.util.GsonUtils;
import com.luckykuang.rabbitmq.vo.CreateOrderVO;
import com.luckykuang.rabbitmq.vo.MessageVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * @author luckykuang
 * @date 2023/9/8 15:08
 */
@Service
public class OrderServiceImpl implements OrderService {

    private static final Gson GSON = GsonUtils.getGsonInstance();

    @Resource
    private DelaySendService delaySendService;

    public void createOrder(CreateOrderVO order) {

        // 参数校验，获取图书实际价格等
        BigDecimal bookPrice = new BigDecimal("99.00");

        // 根据规则生成订单编号，下单，插入数据库中
        // 拿到订单编号，发送下单消息到rabbitmq中
        String orderNo = "100000000201808191000982091";
        order.setOrderNo(orderNo);
        order.setPrice(bookPrice);

        // 封装消息内容
        MessageVO messageVO = new MessageVO();
        messageVO.setId(UUID.randomUUID().toString());
        messageVO.setContent(GSON.toJson(order));
        // 三十分钟时效，自动取消订单
        long time = 30 * 60 * 1000L;
        messageVO.setTtl(time);
        delaySendService.sendDelayMessage(messageVO);
    }
}
