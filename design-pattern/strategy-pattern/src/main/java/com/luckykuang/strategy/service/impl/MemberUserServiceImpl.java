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

package com.luckykuang.strategy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luckykuang.strategy.mapper.MemberUserMapper;
import com.luckykuang.strategy.model.MemberUser;
import com.luckykuang.strategy.service.MemberUserService;
import com.luckykuang.strategy.context.MemberContext;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * @author luckykuang
 * @date 2023/6/20 15:05
 */
@Service
public class MemberUserServiceImpl extends ServiceImpl<MemberUserMapper,MemberUser> implements MemberUserService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Boolean> pay(Long id, BigDecimal amount) {
        MemberUser memberUser = getById(id);
        if (memberUser == null){
            throw new IllegalArgumentException("用户不存在...");
        }
        // 获取赠送金额
        BigDecimal giftAmount = MemberContext.getMemberGiftAmount(memberUser.getMemberLevel(), amount);
        // 更新余额
        memberUser.setAmount(memberUser.getAmount().add(amount).add(giftAmount));
        return ResponseEntity.ok(updateById(memberUser));
    }
}
