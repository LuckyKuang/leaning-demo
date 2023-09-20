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

package com.luckykuang.serial.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 串口实体
 * @author luckykuang
 * @date 2023/9/18 13:45
 */
@Data
public class SerialPortParam implements Serializable {

    @Serial
    private static final long serialVersionUID = 1932985131199246884L;

    /**
     * 串口号
     */
    @NotBlank
    private String serialPort;
    /**
     * 波特率
     */
    @NotNull
    private Integer baudRate;
    /**
     * 数据位
     */
    @NotNull
    private Integer dataBit;
    /**
     * 停止位
     * 参见 {@link com.fazecast.jSerialComm.SerialPort} Number of Stop Bits
     */
    @NotNull
    private Integer stopBit;
    /**
     * 校验值
     * 参见 {@link com.fazecast.jSerialComm.SerialPort} Parity Values
     */
    @NotNull
    private Integer verifyValue;
}
