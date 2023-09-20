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

package com.luckykuang.serial.listener.callback;

import com.luckykuang.serial.util.ConvertHexStrAndStrUtils;
import com.luckykuang.serial.util.SerialPortUtils;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 十六进制数据返回处理
 * @author luckykuang
 * @date 2023/9/12 17:16
 */
@Slf4j
public class SerialPortHexCallback {
    public void dataAvailable(String serialPortName) {
        try {
            // 当前监听器监听到的串口返回数据 back
            byte[] back = SerialPortUtils.readSerialPortData(serialPortName);
            String hexStr = ConvertHexStrAndStrUtils.bytesToHexStr(back);
            log.info("机器返回 串口号：{}，时间：{}，十六进制数据：{}",
                    serialPortName,
                    (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())),
                    hexStr);
        } catch (Exception e) {
            log.error("串口接收异常",e);
        }
    }
}
