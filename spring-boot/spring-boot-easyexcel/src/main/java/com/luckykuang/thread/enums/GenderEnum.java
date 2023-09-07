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

package com.luckykuang.thread.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

/**
 * @author luckykuang
 * @date 2023/7/4 14:57
 */
@Getter
@AllArgsConstructor
public enum GenderEnum {
    /**
     * 未知
     */
    UNKNOWN(0, "未知"),

    /**
     * 男性
     */
    MALE(1, "男性"),

    /**
     * 女性
     */
    FEMALE(2, "女性");

    private final Integer value;

    @JsonFormat
    private final String description;

    /**
     * 通过 value 获取枚举值
     * @param value value值
     * @return 枚举值
     */
    public static GenderEnum convert(Integer value) {
        return Stream.of(values())
                .filter(bean -> bean.value.equals(value))
                .findAny()
                .orElse(UNKNOWN);
    }

    /**
     * 通过 description 获取枚举值
     * @param description description值
     * @return 枚举值
     */
    public static GenderEnum convert(String description) {
        return Stream.of(values())
                .filter(bean -> bean.description.equals(description))
                .findAny()
                .orElse(UNKNOWN);
    }
}
