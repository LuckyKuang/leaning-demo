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

package com.luckykuang.strategy.factory;

import com.luckykuang.strategy.enums.MemberEnum;
import com.luckykuang.strategy.strategy.member.MemberStrategy;
import com.luckykuang.strategy.strategy.member.impl.IntermediateStrategy;
import com.luckykuang.strategy.strategy.member.impl.JuniorStrategy;
import com.luckykuang.strategy.strategy.member.impl.OrdinaryStrategy;
import com.luckykuang.strategy.strategy.member.impl.SeniorStrategy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 会员工厂方法
 * @author luckykuang
 * @date 2023/6/20 11:39
 */
public class MemberFactory {
    private final Map<Integer, MemberStrategy> memberMap;
    private static final MemberFactory INSTANCE = new MemberFactory();
    public static MemberFactory getInstance(){
        return INSTANCE;
    }

    public MemberFactory(){
        // 实例化缓存
        memberMap = new ConcurrentHashMap<>();
        // 将对应的等级放入缓存
        memberMap.put(MemberEnum.ORDINARY_MEMBER.getLevel(), new OrdinaryStrategy());
        memberMap.put(MemberEnum.JUNIOR_MEMBER.getLevel(), new JuniorStrategy());
        memberMap.put(MemberEnum.INTERMEDIATE_MEMBER.getLevel(), new IntermediateStrategy());
        memberMap.put(MemberEnum.SENIOR_MEMBER.getLevel(), new SeniorStrategy());
    }

    /**
     * 通过等级获取对应会员策略
     * @param level 会员等级
     * @return 会员策略
     */
    public MemberStrategy getMemberStrategy(Integer level){
        return memberMap.get(level);
    }
}
