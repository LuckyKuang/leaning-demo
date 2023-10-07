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

package com.luckykuang.strategy.strategy.payment.impl;

import com.luckykuang.strategy.strategy.payment.PaymentStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 支付宝支付具体实现
 * @author luckykuang
 * @date 2023/10/7 9:50
 */
@Slf4j
@Service
public class AlipayStrategy implements PaymentStrategy {
    @Override
    public String createOrder(BigDecimal amount) {
        // 调用支付宝创建订单接口的具体实现
        String orderId = "alipay_order_12345"; // 这里假设返回的订单ID
        log.info("调用支付宝创建订单接口，支付金额：" + amount.toString());
        return orderId;
    }

    @Override
    public String queryResult(String orderId) {
        // 调用支付宝查询订单结果接口的具体实现
        log.info("调用支付宝查询订单结果接口，订单ID：" + orderId);
        return "Paid";
    }
}
