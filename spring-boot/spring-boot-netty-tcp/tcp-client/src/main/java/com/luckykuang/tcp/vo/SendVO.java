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

package com.luckykuang.tcp.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author luckykuang
 * @date 2023/11/21 16:53
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SendVO extends ConnectVO {
    /**
     * 发送数据
     */
    private String data;
    /**
     * 是否添加回车换行符号，默认不添加
     */
    private Boolean enter = Boolean.FALSE;
}
