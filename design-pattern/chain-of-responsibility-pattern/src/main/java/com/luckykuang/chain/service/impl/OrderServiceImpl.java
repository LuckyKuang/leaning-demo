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

package com.luckykuang.chain.service.impl;

import com.luckykuang.chain.handler.OrderHandler;
import com.luckykuang.chain.handler.impl.OrderHandlerA;
import com.luckykuang.chain.handler.impl.OrderHandlerB;
import com.luckykuang.chain.handler.impl.OrderHandlerC;
import com.luckykuang.chain.request.OrderReq;
import com.luckykuang.chain.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * @author luckykuang
 * @date 2023/7/21 13:48
 */
@Service
public class OrderServiceImpl implements OrderService {
    @Override
    public ResponseEntity<String> orderRefund(OrderReq orderReq) {

        // 创建处理器对象
        OrderHandler orderHandlerA = new OrderHandlerA();
        OrderHandler orderHandlerB = new OrderHandlerB();
        OrderHandler orderHandlerC = new OrderHandlerC();

        // 构建责任链(A -> B -> C)
        orderHandlerA.setNextHandler(orderHandlerB);
        orderHandlerB.setNextHandler(orderHandlerC);

        // 处理退款请求
        orderHandlerA.handleRequest(orderReq);

        return ResponseEntity.ok("退款申请提交成功，符合条件的会在三个工作日内到账！");
    }
}
