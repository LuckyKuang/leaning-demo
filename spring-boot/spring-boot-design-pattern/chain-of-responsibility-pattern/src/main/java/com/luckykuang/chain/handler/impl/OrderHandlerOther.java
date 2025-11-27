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

package com.luckykuang.chain.handler.impl;

import com.luckykuang.chain.request.OrderReq;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 人工审核退款
 * @author luckykuang
 * @date 2023/7/25 17:54
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderHandlerOther {

    /**
     * 处理请求
     * @param req 订单请求
     */
    public static void processRequest(OrderReq req) {
        log.info("存入人工审核退款表 level:{},amount:{}", req.getLevel(), req.getAmount());
        // 此处写逻辑，将不符合自动退款条件的退款，进行人工审核
        // ......
    }
}
