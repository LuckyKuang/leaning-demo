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

package com.luckykuang.chain.handler;

import com.luckykuang.chain.handler.impl.OrderHandlerOther;
import com.luckykuang.chain.request.OrderReq;
import lombok.extern.slf4j.Slf4j;

/**
 * @author luckykuang
 * @date 2023/7/21 9:38
 */
@Slf4j
public abstract class OrderHandler {
    private OrderHandler nextOrderHandler;

    /**
     * 设置链路节点关联
     * @param nextOrderHandler 下一个节点
     */
    public void setNextHandler(OrderHandler nextOrderHandler) {
        this.nextOrderHandler = nextOrderHandler;
    }

    /**
     * 处理请求
     * @param req 请求
     */
    public void handleRequest(OrderReq req) {
        // 如果满足条件
        if (checkRequest(req)) {
            processRequest(req);
        }
        // 如果不满足上个节点的条件，继续递归寻找下个节点
        else if (nextOrderHandler != null) {
            nextOrderHandler.handleRequest(req);
        }else {
            // 条件都不满足，进行人工审核退款
            OrderHandlerOther.processRequest(req);
        }
    }

    protected abstract boolean checkRequest(OrderReq req);

    protected abstract void processRequest(OrderReq req);
}
