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

package com.luckykuang.strategy.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

/**
 * @author luckykuang
 * @date 2023/6/21 17:07
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateUtils {

    /**
     * 校验日期格式是否正确，正确的返回
     * @param dateStr 日期字符串
     * @param format 格式
     * @param regex 正则
     * @return 正确的日期
     */
    public static String validDateFormat(String dateStr, String format, String regex) {
        boolean matches =  Pattern.matches(regex, dateStr);
        if(!matches){
            log.info("日期["+dateStr+"]正则校验异常...");
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        // 设置日期格式转的严谨性
        sdf.setLenient(false);
        try {
            sdf.parse(dateStr);
        } catch (ParseException e) {
            log.info("日期["+dateStr+"]格式校验异常...");
            return "";
        }
        return dateStr;
    }
}
