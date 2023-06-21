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

package com.luckykuang.strategy.controller;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.luckykuang.strategy.service.MemberUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * @author luckykuang
 * @date 2023/6/20 14:58
 */
@RestController
@RequestMapping("api/v1/strategy")
@RequiredArgsConstructor
public class TestController {

    private final MemberUserService memberUserService;

    /**
     * 假设用户已经是对应的会员了，所以不考虑变成会员的场景
     * 举例场景一(写法一)：
     * 会员充值送金额 - 会员升级场景暂时不考虑，熟悉策略模式为主
     * 普通会员-充值满1000不送
     * 初级会员-充值满1000送100-充值满2000送250-充值满5000送1000
     * 中级会员-充值满1000送200-充值满2000送500-充值满5000送1500
     * 高级会员-充值满1000送300-充值满2000送700-充值满5000送2000
     */
    @PostMapping("save")
    public ResponseEntity<Boolean> pay(Long id, @JsonSerialize(using = ToStringSerializer.class) BigDecimal amount){
        return memberUserService.pay(id,amount);
    }
}
