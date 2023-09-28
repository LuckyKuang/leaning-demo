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

package com.luckykuang.ping.vo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author luckykuang
 * @date 2023/9/22 18:43
 */
@Data
public class UdpSendVO {
    /**
     * IP地址前缀 如：192.168.1
     * 类型为组播时，此字段填充组播地址 如：224.0.0.1
     */
    private String ipPrefix;
    /**
     * 端口号
     */
    @NotNull
    private Integer port;
    /**
     * 发送的数据 默认为空字符串
     */
    private String data = "";
    /**
     * 类型 1-单播 2-广播 3-组播
     */
    @NotNull
    private Integer type;
}
