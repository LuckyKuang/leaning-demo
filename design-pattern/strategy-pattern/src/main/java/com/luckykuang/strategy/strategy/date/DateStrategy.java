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

package com.luckykuang.strategy.strategy.date;

/**
 * 日期策略抽象类
 * @author luckykuang
 * @date 2023/6/21 10:08
 */
public abstract class DateStrategy {

    /**
     * 特定业务需要，校验日期格式并替换为年，如果是[2023-06]或者[2023年]则替换为[2023]
     * @param dateStr 日期字符串
     * @return 正确的年
     */
    public String checkDateFormatReplaceToYear(String dateStr) {
        return "";
    }

    /**
     * 校验日期格式，正确的返回
     * @param dateStr 日期字符串
     * @return 正确的时间
     */
    public abstract String checkDateFormat(String dateStr);
}
