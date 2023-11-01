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

package com.luckykuang.statemachine.service.impl;

import com.luckykuang.statemachine.entity.Order;
import com.luckykuang.statemachine.enums.OrderStatus;
import com.luckykuang.statemachine.enums.OrderStatusChangeEvent;
import com.luckykuang.statemachine.mapper.OrderMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.statemachine.annotation.OnTransition;
import org.springframework.statemachine.annotation.WithStateMachine;
import org.springframework.stereotype.Component;

/**
 * @author luckykuang
 * @date 2023/10/9 16:29
 */
@Slf4j
@Component("orderStateListener")
@WithStateMachine(name = "orderStateMachine")
public class OrderStateListenerImpl {
    @Resource
    private OrderMapper orderMapper;

    @OnTransition(source = "WAITING_FOR_PAYMENT", target = "WAITING_FOR_DELIVER")
    public void payTransition(Message<OrderStatusChangeEvent> message) {
        Order order = (Order) message.getHeaders().get("order");
        log.info("支付，状态机反馈信息：{}",  message.getHeaders().toString());
        // 更新订单
        order.setStatus(OrderStatus.WAITING_FOR_DELIVER.getKey());
        orderMapper.updateById(order);
        //TODO 其他业务
    }
    @OnTransition(source = "WAITING_FOR_DELIVER", target = "WAITING_FOR_RECEIVE")
    public void deliverTransition(Message<OrderStatusChangeEvent> message) {
        Order order = (Order) message.getHeaders().get("order");
        log.info("发货，状态机反馈信息：{}",  message.getHeaders().toString());
        // 更新订单
        order.setStatus(OrderStatus.WAITING_FOR_RECEIVE.getKey());
        orderMapper.updateById(order);
        //TODO 其他业务
    }
    @OnTransition(source = "WAITING_FOR_RECEIVE", target = "COMPLETED")
    public void receiveTransition(Message<OrderStatusChangeEvent> message) {
        Order order = (Order) message.getHeaders().get("order");
        log.info("确认收货，状态机反馈信息：{}",  message.getHeaders().toString());
        // 更新订单
        order.setStatus(OrderStatus.COMPLETED.getKey());
        orderMapper.updateById(order);
        //TODO 其他业务
    }
}
