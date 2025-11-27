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

import com.luckykuang.chain.enums.OrderEnum;
import com.luckykuang.chain.handler.OrderHandler;
import com.luckykuang.chain.request.OrderReq;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 第二级别
 * @author luckykuang
 * @date 2023/7/21 11:03
 */
@Slf4j
public class OrderHandlerB extends OrderHandler {
    /**
     * 抽象方法，判断是否满足条件
     * @param req 订单请求
     * @return 是否满足条件
     */
    protected boolean checkRequest(OrderReq req) {
        // 第二级别且小于等于该级别设置的金额
        return Objects.equals(OrderEnum.INTERMEDIATE.getLevel(), req.getLevel())
                && OrderEnum.INTERMEDIATE.getAmount().compareTo(req.getAmount()) > -1;
    }

    /**
     * 抽象方法，处理请求
     * @param req 订单请求
     */
    protected void processRequest(OrderReq req) {
        // 满足第二级别退款条件，触发此节点，直接给用户退款
        log.info("满足第二级别退款条件 level:{},amount:{}", req.getLevel(),req.getAmount());
        // 此处写退了逻辑
        // ......
    }
}
