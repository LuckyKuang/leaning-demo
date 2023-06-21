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

package com.luckykuang.strategy.strategy.member;

import java.math.BigDecimal;

/**
 * 会员策略统一抽象
 * 如果所有实现类都是一样的方法，就可以用接口表示
 * 如果有的实现类用的不同的方法，就用抽象类表示，抽象方法集体用，普通方法极个别实现类用
 * @author luckykuang
 * @date 2023/6/20 11:40
 */
public interface MemberStrategy {

    /**
     * 计算赠送规则
     * @param amount 需要计算的金额
     * @return 对应会员的赠送金额
     */
    BigDecimal giftRules(BigDecimal amount);
}
