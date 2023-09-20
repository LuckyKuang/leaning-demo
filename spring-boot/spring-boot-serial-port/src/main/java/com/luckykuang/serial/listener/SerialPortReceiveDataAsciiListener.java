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

package com.luckykuang.serial.listener;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.luckykuang.serial.listener.callback.SerialPortAsciiCallback;
import com.luckykuang.serial.util.SerialPortUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

import static com.fazecast.jSerialComm.SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
import static com.fazecast.jSerialComm.SerialPort.LISTENING_EVENT_PORT_DISCONNECTED;

/**
 * 串口接收数据监听器(十进制)
 * @author luckykuang
 * @date 2023/9/12 17:16
 */
@Slf4j
public class SerialPortReceiveDataAsciiListener implements SerialPortDataListener {
    private final SerialPortAsciiCallback serialPortAsciiCallback;

    public SerialPortReceiveDataAsciiListener(SerialPortAsciiCallback serialPortAsciiCallback) {
        this.serialPortAsciiCallback = serialPortAsciiCallback;
    }

    @Override
    public int getListeningEvents() {
        // 必须是return这个才会开启串口工具的监听
        return LISTENING_EVENT_DATA_AVAILABLE | LISTENING_EVENT_PORT_DISCONNECTED;
    }

    /**
     * 每当getListeningEvents()方法指定的一个或多个串行端口事件发生时调用。
     * @param serialPortEvent A {@link SerialPortEvent} object containing information and/or data about the serial events that occurred.
     */
    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        // 监听事件类型
        int eventType = serialPortEvent.getEventType();
        SerialPort serialPort = serialPortEvent.getSerialPort();
        // 串口断开
        if (Objects.equals(eventType,LISTENING_EVENT_PORT_DISCONNECTED)){
            serialPort.closePort();
            SerialPortUtils.SERIAL_PORT_STATUS.put(serialPort.getSystemPortName(),false);
            SerialPortUtils.SERIAL_PORT_CACHE.remove(serialPort.getSystemPortName());
            return;
        }
        // 可用的监听事件数据
        if (serialPortAsciiCallback != null && Objects.equals(eventType,LISTENING_EVENT_DATA_AVAILABLE)) {
            serialPortAsciiCallback.dataAvailable(serialPort.getSystemPortName());
        }
    }
}
