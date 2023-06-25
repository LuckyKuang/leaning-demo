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

package com.luckykuang.strategy.strategy.date.impl;

import com.luckykuang.strategy.strategy.date.DateStrategy;
import com.luckykuang.strategy.utils.DateUtils;

/**
 * 例如：202306250613
 * @author luckykuang
 * @date 2023/6/21 11:02
 */
public class YyyyMmDdHhMmStrategy extends DateStrategy {
    @Override
    public String checkDateFormat(String dateStr) {
        return DateUtils.validDateFormat(
                dateStr,
                "yyyyMMddHHmm",
                "^([1-9]\\d{3})((0?[1-9])|(1[0-2]))([0-3]?[0-9])((0?[1-9])|(1[0-9])|(2[0-4]))([0-5][0-9]?)$");
    }
}
