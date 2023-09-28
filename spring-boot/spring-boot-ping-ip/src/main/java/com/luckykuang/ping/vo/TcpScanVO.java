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

import lombok.Data;

/**
 * @author luckykuang
 * @date 2023/9/22 18:43
 */
@Data
public class TcpScanVO {
    /**
     * IP地址前缀 如：192.168.1
     */
    private String ipPrefix;
    /**
     * 端口号
     */
    private Integer port;
    /**
     * IP地址后缀起始位置 1~254 起始位置一定要小于结束位置
     */
    private Integer startAddr;
    /**
     * IP地址后缀结束位置 2~255
     */
    private Integer endAddr;
}
