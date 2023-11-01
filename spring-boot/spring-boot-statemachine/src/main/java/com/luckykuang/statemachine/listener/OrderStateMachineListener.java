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

package com.luckykuang.statemachine.listener;

import com.luckykuang.statemachine.enums.OrderStatus;
import com.luckykuang.statemachine.enums.OrderStatusChangeEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

/**
 * 订单状态监听器
 * @author luckykuang
 * @date 2023/10/26 9:37
 */
@Slf4j
@Component
public class OrderStateMachineListener implements StateMachineListener<OrderStatus, OrderStatusChangeEvent> {
    @Override
    public void stateChanged(State<OrderStatus, OrderStatusChangeEvent> from, State<OrderStatus, OrderStatusChangeEvent> to) {
        log.info("订单状态变更：{} -> {}",from,to);
    }

    @Override
    public void stateEntered(State<OrderStatus, OrderStatusChangeEvent> state) {
        log.info("进入订单状态：{}", state);
    }

    @Override
    public void stateExited(State<OrderStatus, OrderStatusChangeEvent> state) {
        log.info("离开订单状态：{}", state);
    }

    @Override
    public void eventNotAccepted(Message<OrderStatusChangeEvent> event) {
        log.warn("无法接受的事件：{}", event);
    }

    @Override
    public void transition(Transition<OrderStatus, OrderStatusChangeEvent> transition) {
        log.info("转换发生时通知：{}",transition);
    }

    @Override
    public void transitionStarted(Transition<OrderStatus, OrderStatusChangeEvent> transition) {
        log.info("转换开始时通知：{}",transition);
    }

    @Override
    public void transitionEnded(Transition<OrderStatus, OrderStatusChangeEvent> transition) {
        log.info("转换结束时通知：{}",transition);
    }

    @Override
    public void stateMachineStarted(StateMachine<OrderStatus, OrderStatusChangeEvent> stateMachine) {
        log.info("状态机启动时通知：{}",stateMachine);
    }

    @Override
    public void stateMachineStopped(StateMachine<OrderStatus, OrderStatusChangeEvent> stateMachine) {
        log.info("当状态机停止时通知：{}",stateMachine);
    }

    @Override
    public void stateMachineError(StateMachine<OrderStatus, OrderStatusChangeEvent> stateMachine, Exception exception) {
        log.warn("当状态机出现无法恢复的错误时通知：{}",stateMachine,exception);
    }

    @Override
    public void extendedStateChanged(Object key, Object value) {
        log.info("当添加、修改或删除扩展状态变量时通知 key：{},value：{}",key,value);
    }

    @Override
    public void stateContext(StateContext<OrderStatus, OrderStatusChangeEvent> stateContext) {
        log.info("StateContext的阶段：{}",stateContext);
    }
}
