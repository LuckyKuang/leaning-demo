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

package com.luckykuang.chain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * 场景：某多多为了讨好用户，对不同级别的用户退款金额低于设置的值，直接默认同意退款(本案例暂时不考虑多次退款薅羊毛的场景)
 * 1.第一级别，比如没充钱的用户，退款金额不超过配置金额，直接退款
 * 2.第二级别，比如信用分平台达标且没充钱的用户，退款金额不超过配置金额，直接退款
 * 3.第三级别，比如信用分平台达标且充了年费会员的用户，退款金额不超过配置金额，直接退款
 * 4.其他，不在以上级别范围内，走人工审核
 * @author luckykuang
 * @date 2023/7/25 15:05
 */
@Getter
@AllArgsConstructor
public enum OrderEnum {
    JUNIOR(1, new BigDecimal("10.00")),
    INTERMEDIATE(2, new BigDecimal("50.00")),
    SENIOR(3, new BigDecimal("100.00")),
    ;

    /**
     * 等级
     */
    private final Integer level;
    /**
     * 金额
     */
    private final BigDecimal amount;
}
