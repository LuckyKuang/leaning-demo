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

package com.luckykuang.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author luckykuang
 * @date 2024/4/15 16:31
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface Limit {
    // 资源key
    String key() default "";

    // 最多访问次数
    double permitsPerSecond();

    // 时间
    long timeout();

    // 时间类型
    TimeUnit timeunit() default TimeUnit.MILLISECONDS;

    // 提示信息
    String msg() default "系统繁忙,请稍后再试";
}
