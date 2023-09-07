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

package com.luckykuang.strategy.context;

import com.luckykuang.strategy.factory.MemberFactory;
import com.luckykuang.strategy.strategy.member.MemberStrategy;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 会员环境类 用于抽取业务方法
 * @author luckykuang
 * @date 2023/6/20 14:44
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberContext {

    /**
     * 根据会员等级和金额，计算对应会员赠送金额
     * @param level 会员等级
     * @param amount 金额
     * @return 对应会员赠送金额
     */
    public static BigDecimal getMemberGiftAmount(Integer level, BigDecimal amount){
        MemberStrategy memberStrategy = MemberFactory.getInstance().getMemberStrategy(level);
        if (memberStrategy == null){
            throw new IllegalArgumentException("会员等级输入有误...");
        }
        return memberStrategy.giftRules(amount);
    }
}
