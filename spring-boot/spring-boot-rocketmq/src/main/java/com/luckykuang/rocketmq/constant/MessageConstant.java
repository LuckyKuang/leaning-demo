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

package com.luckykuang.rocketmq.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author luckykuang
 * @date 2023/10/22 23:00
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MessageConstant {

    // 延迟级别对应延迟时间 1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
    // level:0代表无延迟  1  2  3   4   5  6  7  8  9  10 11 12 13 14  15  16  17 18
    public static final Integer FIVE_SECOND = 0;
    public static final String RETRY_PREFIX = "MEMBER";
}
