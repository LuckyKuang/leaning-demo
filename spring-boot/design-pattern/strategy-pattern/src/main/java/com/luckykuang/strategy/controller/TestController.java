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

import com.luckykuang.strategy.service.MemberUserService;
import com.luckykuang.strategy.vo.DateListVO;
import com.luckykuang.strategy.vo.DateVO;
import com.luckykuang.strategy.vo.MemberUserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public ResponseEntity<Boolean> pay(@RequestBody @Validated MemberUserVO vo){
        return memberUserService.pay(vo.id(),vo.amount());
    }

    /**
     * 举例场景二(写法二)：
     * ocr识别某个数据报表，将里面正确的时间过滤出来 - 不考虑其他具体场景，比如错误的数据处理暂时不考虑，熟悉策略模式为主
     * 假设集合里面都是[yyyy-MM-dd/yyyyMMdd]的日期字符串，再参杂一些不符合该格式的字符串
     * 只获取正确格式[yyyy-MM-dd/yyyyMMdd]的字符串
     */
    @PostMapping("checkDate")
    public ResponseEntity<List<DateListVO>> checkDate(@RequestBody @Validated DateVO vo){
        return memberUserService.checkDate(vo);
    }

    /**
     * 举例场景三(写法二)：用的是年独有的实现
     * 将集合里面的[yyyy-MM/yyyyMM/yyyy年/yyyy]都只获取[yyyy]格式
     */
    @PostMapping("checkDateYear")
    public ResponseEntity<List<DateListVO>> checkDateYear(@RequestBody @Validated DateVO vo){
        return memberUserService.checkDateYear(vo);
    }
}
