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

package com.luckykuang.serial.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author luckykuang
 * @date 2023/9/19 10:09
 */
@Data
public class SerialPortParamVO {

    /**
     * 串口号
     */
    @NotBlank
    private String serialPort;

    /**
     * 类型 1-十进制字符串 2-十六进制字符串
     */
    @NotNull
    private Integer type;

    /**
     * 数据
     */
    @NotBlank
    private String data;

    /**
     * 是否添加回车换行(true-添加 false-不添加)
     */
    private Boolean enter = true;
}
