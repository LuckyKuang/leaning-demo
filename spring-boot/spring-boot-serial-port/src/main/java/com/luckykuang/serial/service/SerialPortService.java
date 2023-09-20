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

package com.luckykuang.serial.service;

import com.luckykuang.serial.entity.SerialPortParam;
import com.luckykuang.serial.vo.SerialPortParamVO;
import com.luckykuang.serial.vo.SerialPortStatusVO;

import java.util.List;

/**
 * @author luckykuang
 * @date 2023/9/19 10:52
 */
public interface SerialPortService {
    List<String> getSerialPortList();

    SerialPortParam getSerialPort(String serialPortName);

    String setSerialPort(SerialPortStatusVO statusVO);

    String sendData(SerialPortParamVO paramVO);
}
