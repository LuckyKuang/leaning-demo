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

package com.luckykuang.strategy.strategy.member.impl;

import com.luckykuang.strategy.strategy.member.MemberStrategy;

import java.math.BigDecimal;

/**
 * 初级会员策略
 * @author luckykuang
 * @date 2023/6/20 14:06
 */
public class JuniorStrategy implements MemberStrategy {

    @Override
    public BigDecimal giftRules(BigDecimal amount) {
        // 充值满5000送1000
        if (amount.compareTo(new BigDecimal("5000.00")) > -1){
            return new BigDecimal("1000.00");
        }
        // 充值满2000送250
        else if (amount.compareTo(new BigDecimal("2000.00")) > -1){
            return new BigDecimal("250.00");
        }
        // 充值满1000送100
        else if (amount.compareTo(new BigDecimal("1000.00")) > -1){
            return new BigDecimal("100.00");
        }
        // 其他不送
        return BigDecimal.ZERO;
    }
}
