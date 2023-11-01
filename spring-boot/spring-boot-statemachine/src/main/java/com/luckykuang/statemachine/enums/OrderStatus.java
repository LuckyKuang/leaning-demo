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

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 订单状态
 * @author luckykuang
 * @date 2023/10/9 15:21
 */
@Getter
@AllArgsConstructor
public enum OrderStatus {
    WAITING_FOR_PAYMENT(1, "待支付"),
    WAITING_FOR_DELIVER(2, "待发货"),
    WAITING_FOR_RECEIVE(3, "待收货"),
    COMPLETED(4, "已完成");

    private final Integer key;
    private final String desc;

    public static OrderStatus getByKey(Integer key) {
        for (OrderStatus e : values()) {
            if (e.getKey().equals(key)) {
                return e;
            }
        }
        throw new RuntimeException("enum not exists.");
    }
}
