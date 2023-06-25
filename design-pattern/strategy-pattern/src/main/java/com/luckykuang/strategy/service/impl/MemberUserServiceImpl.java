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
import com.luckykuang.strategy.context.DateContext;
import com.luckykuang.strategy.context.MemberContext;
import com.luckykuang.strategy.mapper.MemberUserMapper;
import com.luckykuang.strategy.model.MemberUser;
import com.luckykuang.strategy.service.MemberUserService;
import com.luckykuang.strategy.strategy.date.impl.*;
import com.luckykuang.strategy.vo.DateListVO;
import com.luckykuang.strategy.vo.DateVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    public ResponseEntity<List<DateListVO>> checkDate(DateVO vo) {
        List<DateListVO> listVOS = new ArrayList<>();
        vo.dateList().forEach(dateVO -> {
            // 校验[yyyy-MM-dd]格式
            DateContext context1 = new DateContext(new YyyyMmDdSymbolsStrategy());
            String dateFormat1 = context1.checkDateFormat(dateVO.dateStr());
            if (StringUtils.isNotBlank(dateFormat1)){
                listVOS.add(new DateListVO(dateVO.id(),dateFormat1));
                return;
            }
            // 校验[yyyyMMdd]格式
            DateContext context2 = new DateContext(new YyyyMmDdStrategy());
            String dateFormat2 = context2.checkDateFormat(dateVO.dateStr());
            if (StringUtils.isNotBlank(dateFormat2)){
                listVOS.add(new DateListVO(dateVO.id(),dateFormat2));
            }
        });
        return ResponseEntity.ok(listVOS);
    }

    @Override
    public ResponseEntity<List<DateListVO>> checkDateYear(DateVO vo) {
        List<DateListVO> listVOS = new ArrayList<>();
        vo.dateList().forEach(dateVO -> {
            // 校验[yyyy-MM]格式
            DateContext context1 = new DateContext(new YyyyMmSymbolsStrategy());
            String dateFormat1 = context1.checkDateFormatReplaceToYear(dateVO.dateStr());
            if (StringUtils.isNotBlank(dateFormat1)){
                listVOS.add(new DateListVO(dateVO.id(),dateFormat1));
                return;
            }
            // 校验[yyyyMM]格式
            DateContext context2 = new DateContext(new YyyyMmStrategy());
            String dateFormat2 = context2.checkDateFormatReplaceToYear(dateVO.dateStr());
            if (StringUtils.isNotBlank(dateFormat2)){
                listVOS.add(new DateListVO(dateVO.id(),dateFormat2));
                return;
            }
            // 校验[yyyy年]格式
            DateContext context3 = new DateContext(new YyyySymbolsStrategy());
            String dateFormat3 = context3.checkDateFormatReplaceToYear(dateVO.dateStr());
            if (StringUtils.isNotBlank(dateFormat3)){
                listVOS.add(new DateListVO(dateVO.id(),dateFormat3));
                return;
            }
            // 校验[yyyy]格式
            DateContext context4 = new DateContext(new YyyyStrategy());
            String dateFormat4 = context4.checkDateFormatReplaceToYear(dateVO.dateStr());
            if (StringUtils.isNotBlank(dateFormat4)){
                listVOS.add(new DateListVO(dateVO.id(),dateFormat4));
            }
        });
        return ResponseEntity.ok(listVOS);
    }
}
