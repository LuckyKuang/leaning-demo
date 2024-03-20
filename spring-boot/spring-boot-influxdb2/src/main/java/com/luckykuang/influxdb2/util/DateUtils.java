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

package com.luckykuang.influxdb2.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * @author luckykuang
 * @date 2024/1/23 18:13
 */
public final class DateUtils {

    private DateUtils() {}

    /**
     * LocalDateTime类型转换String类型
     * @param localDateTime 时间类型
     * @param regEx         正则，如：yyyy-MM-dd HH:mm:ss
     */
    public static String localDateTimeToString(LocalDateTime localDateTime,String regEx) {
        Objects.requireNonNull(localDateTime, "localDateTime");
        Objects.requireNonNull(regEx, "regEx");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern(regEx);
        return localDateTime.format(fmt);
    }

    /**
     * String类型转换LocalDateTime类型
     * @param dateStr   时间字符串
     * @param regEx     正则，如：yyyy-MM-dd HH:mm:ss
     */
    public static LocalDateTime stringToLocalDateTime(String dateStr, String regEx) {
        Objects.requireNonNull(dateStr, "dateStr");
        Objects.requireNonNull(regEx, "regEx");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern(regEx);
        return LocalDateTime.parse(dateStr, fmt);
    }
}
