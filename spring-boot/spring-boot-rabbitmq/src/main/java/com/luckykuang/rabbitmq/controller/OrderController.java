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

import com.luckykuang.rabbitmq.service.OrderService;
import com.luckykuang.rabbitmq.vo.CreateOrderVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author luckykuang
 * @date 2023/9/8 15:21
 */
@Tag(name = "订单")
@RestController
@RequestMapping(value = "/api/order")
public class OrderController {

    @Resource
    private OrderService orderService;

    @Operation(summary = "模拟用户下单", description = "下单后三十分钟未支付，系统自动取消订单")
    @PostMapping(value = "/msg")
    public String sendDelayMessage(@Parameter(name = "订单", description = "order", required = true)
                                   @RequestBody CreateOrderVO order) {
        orderService.createOrder(order);
        return "success";
    }
}
