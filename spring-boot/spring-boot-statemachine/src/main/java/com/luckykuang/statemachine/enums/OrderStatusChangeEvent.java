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

package com.luckykuang.statemachine.enums;

/**
 * 订单状态事件变更枚举
 * @author luckykuang
 * @date 2023/10/9 15:42
 */
public enum OrderStatusChangeEvent {
    // 已支付
    PAYMENT_RECEIVED,
    // 发货完成
    SHIPPING_COMPLETED,
    // 确认收货
    RECEIVE_COMPLETED;
}
