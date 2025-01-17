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

import com.luckykuang.serial.entity.SerialPortParam;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * @author luckykuang
 * @date 2023/9/19 11:21
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SerialPortStatusVO extends SerialPortParam {

    @Serial
    private static final long serialVersionUID = -4034317421841204762L;

    /**
     * 串口状态 1-打开 0-关闭
     */
    @NotNull
    private Integer status;
}
