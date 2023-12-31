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

package com.luckykuang.strategy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.luckykuang.strategy.model.MemberUser;
import com.luckykuang.strategy.vo.DateListVO;
import com.luckykuang.strategy.vo.DateVO;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author luckykuang
 * @date 2023/6/20 15:04
 */
public interface MemberUserService extends IService<MemberUser> {
    ResponseEntity<Boolean> pay(Long id, BigDecimal amount);

    ResponseEntity<List<DateListVO>> checkDate(DateVO vo);

    ResponseEntity<List<DateListVO>> checkDateYear(DateVO vo);
}
