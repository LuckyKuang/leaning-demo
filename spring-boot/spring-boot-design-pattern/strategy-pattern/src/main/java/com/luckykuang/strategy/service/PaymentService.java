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

package com.luckykuang.strategy.service;

import java.math.BigDecimal;

/**
 * 支付订单服务接口
 * @author luckykuang
 * @date 2023/10/7 9:47
 */
public interface PaymentService {

    /**
     * 创建支付订单
     *
     * @param paymentChannel 支付渠道
     * @param amount         支付金额
     * @return 订单ID
     */
    String createOrder(String paymentChannel, BigDecimal amount);

    /**
     * 查询支付结果
     *
     * @param paymentChannel 支付渠道
     * @param orderId        订单ID
     * @return 支付结果
     */
    String queryResult(String paymentChannel, String orderId);
}
