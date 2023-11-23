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

package com.luckykuang.udp.constant;

import java.util.concurrent.atomic.AtomicReference;

/**
 * 全局静态属性
 * @author luckykuang
 * @date 2023/11/23 16:49
 */
public interface Constants {
    /**
     * 用于缓存编码信息
     */
    AtomicReference<String> UDP_CODEC = new AtomicReference<>();

    String ASCII = "ascii";
    String HEX = "hex";
}
