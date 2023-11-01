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

package com.luckykuang.statemachine.config;

import com.luckykuang.statemachine.enums.OrderStatus;
import com.luckykuang.statemachine.enums.OrderStatusChangeEvent;
import com.luckykuang.statemachine.listener.OrderStateMachineListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

/**
 * 定义状态机规则和配置状态机
 * @author luckykuang
 * @date 2023/10/9 15:43
 */
@Configuration
@EnableStateMachine(name = "orderStateMachine")
public class OrderStateMachineConfig extends EnumStateMachineConfigurerAdapter<OrderStatus, OrderStatusChangeEvent> {

    /**
     * 配置配置器
     * @param config the {@link StateMachineConfigurationConfigurer}
     * @throws Exception
     */
    @Override
    public void configure(StateMachineConfigurationConfigurer<OrderStatus, OrderStatusChangeEvent> config) throws Exception {
        config.withConfiguration()
                // 指定是否应该自动启动状态机。默认状态机没有自动启动
                .autoStartup(true)
                // 指定要向状态机注册的StateMachineListener。此方法可以多次调用以注册多个侦听器
                .listener(new OrderStateMachineListener());
    }

    /**
     * 配置状态
     * @param states the {@link StateMachineStateConfigurer}
     * @throws Exception
     */
    @Override
    public void configure(StateMachineStateConfigurer<OrderStatus, OrderStatusChangeEvent> states) throws Exception {
        states.withStates()
                // 指定订单初始化状态为：未支付
                .initial(OrderStatus.WAITING_FOR_PAYMENT)
                // 指定状态配置器
                .states(EnumSet.allOf(OrderStatus.class));
    }

    /**
     * 配置状态转换事件关系
     * @param transitions the {@link StateMachineTransitionConfigurer}
     * @throws Exception
     */
    @Override
    public void configure(StateMachineTransitionConfigurer<OrderStatus, OrderStatusChangeEvent> transitions) throws Exception {
        transitions
                // 支付事件：待支付 -> 待发货
                .withExternal().source(OrderStatus.WAITING_FOR_PAYMENT).target(OrderStatus.WAITING_FOR_DELIVER).event(OrderStatusChangeEvent.PAYMENT_RECEIVED)
                .and()
                // 发货事件：待发货 -> 待收货
                .withExternal().source(OrderStatus.WAITING_FOR_DELIVER).target(OrderStatus.WAITING_FOR_RECEIVE).event(OrderStatusChangeEvent.SHIPPING_COMPLETED)
                .and()
                // 收货事件：待收货 -> 已完成
                .withExternal().source(OrderStatus.WAITING_FOR_RECEIVE).target(OrderStatus.COMPLETED).event(OrderStatusChangeEvent.RECEIVE_COMPLETED);
    }
}
