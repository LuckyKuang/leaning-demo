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

package com.luckykuang.serial.util;

import com.fazecast.jSerialComm.SerialPort;
import com.google.common.collect.Lists;
import com.luckykuang.serial.entity.SerialPortParam;
import com.luckykuang.serial.listener.SerialPortOpenListener;
import com.luckykuang.serial.listener.SerialPortReceiveDataAsciiListener;
import com.luckykuang.serial.listener.SerialPortReceiveDataHexListener;
import com.luckykuang.serial.listener.callback.SerialPortAsciiCallback;
import com.luckykuang.serial.listener.callback.SerialPortHexCallback;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author luckykuang
 * @date 2023/9/12 17:15
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SerialPortUtils {

    /**
     * 串口状态缓存
     */
    public static final Map<String, Boolean> SERIAL_PORT_STATUS = new ConcurrentHashMap<>();
    /**
     * 串口缓存
     */
    public static final Map<String, SerialPort> SERIAL_PORT_CACHE = new ConcurrentHashMap<>();

    /**
     * 查找所有可用串口
     */
    public static List<String> getSerialPortList() {
        // 获得当前所有可用串口
        SerialPort[] serialPorts = SerialPort.getCommPorts();
        List<String> portNameList = Lists.newArrayList();
        // 将可用串口名添加到List并返回该List
        for (SerialPort serialPort : serialPorts) {
            portNameList.add(serialPort.getSystemPortName());
        }
        //去重
        portNameList = portNameList.stream().distinct().collect(Collectors.toList());
        return portNameList;
    }

    /**
     * 获取某个串口详细信息
     */
    public static SerialPortParam getSerialPort(String serialPortName) {
        // 获得当前所有可用串口
        SerialPort[] serialPorts = SerialPort.getCommPorts();
        Optional<SerialPort> first = Arrays.stream(serialPorts)
                .filter(serialPort -> Objects.equals(serialPort.getSystemPortName(), serialPortName))
                .findFirst();
        if (first.isEmpty()){
            return null;
        }
        SerialPort serialPort = first.get();
        SerialPortParam param = new SerialPortParam();
        param.setSerialPort(serialPort.getSystemPortName());
        param.setBaudRate(serialPort.getBaudRate());
        param.setDataBit(serialPort.getNumDataBits());
        param.setStopBit(serialPort.getNumStopBits());
        param.setVerifyValue(serialPort.getParity());
        return param;
    }

    /**
     * 打开串口
     */
    public static SerialPort openSerialPort(SerialPortParam serialPortParam) {
        SerialPort serialPort = SerialPort.getCommPort(serialPortParam.getSerialPort());
        // 如果串口已经打开，直接返回即可
        if (serialPort.isOpen()){
            return serialPort;
        }
        // 一次设置所有串口参数 波特率/数据位/停止位/校验值
        serialPort.setComPortParameters(
                serialPortParam.getBaudRate(),
                serialPortParam.getDataBit(),
                serialPortParam.getStopBit(),
                serialPortParam.getVerifyValue());
        // 开启串口，延迟50毫秒
        // 请注意，当尝试打开Serial-over-Bluetooth端口时，如果底层设备驱动程序需要很长时间才能连接，则此方法可能会失败。
        // 在这种情况下，重新尝试此方法可能允许它最终连接并成功。
        serialPort.openPort(50);
        // 禁用流量控制
        serialPort.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);
        // 设置串口读写超时参数。注意，基于时间的写超时只适用于Windows系统。在其他操作系统上没有设置写超时的功能。
        serialPort.setComPortTimeouts(
                // 写阻塞 | 读全阻塞
                SerialPort.TIMEOUT_READ_BLOCKING | SerialPort.TIMEOUT_WRITE_BLOCKING,
                // 读超时时间(毫秒)
                1000,
                // 写超时时间(毫秒)
                1000);
        // 开启串口断开检测监听，用于监听数据和检测串口断开
        serialPort.addDataListener(new SerialPortOpenListener());
        SERIAL_PORT_CACHE.put(serialPort.getSystemPortName(),serialPort);
        SERIAL_PORT_STATUS.put(serialPort.getSystemPortName(),true);
        return serialPort;
    }

    /**
     * 关闭串口
     */
    public static void closeSerialPort(String serialPortName) {
        SerialPort serialPort = SERIAL_PORT_CACHE.get(serialPortName);
        if (serialPort != null && serialPort.isOpen()){
            // 串口打开时开启了监听器，关闭前一定要先移除监听器
            serialPort.removeDataListener();
            serialPort.closePort();
            SERIAL_PORT_CACHE.remove(serialPort.getSystemPortName());
            SERIAL_PORT_STATUS.put(serialPort.getSystemPortName(),false);
        }
    }

    /**
     * 发送字节数组
     * 每次请求新增串口监听，是因为要同时兼容十进制和十六进制
     * 新增监听前移除串口监听，是防止重复监听
     */
    public static void sendSerialPortData(String serialPortName, Integer type, byte[] content) {
        SerialPort serialPort = SERIAL_PORT_CACHE.get(serialPortName);
        if (serialPort != null && serialPort.isOpen()){
            // 开启串口数据监听
            if (Objects.equals(type,1)) {
                serialPort.removeDataListener();
                serialPort.addDataListener(new SerialPortReceiveDataAsciiListener(new SerialPortAsciiCallback()));
            }
            if (Objects.equals(type,2)) {
                serialPort.removeDataListener();
                serialPort.addDataListener(new SerialPortReceiveDataHexListener(new SerialPortHexCallback()));
            }
            serialPort.writeBytes(content, content.length);
        }
    }

    /**
     * 读取字节数组
     */
    public static byte[] readSerialPortData(String serialPortName) {
        SerialPort serialPort = SERIAL_PORT_CACHE.get(serialPortName);
        if (serialPort != null && serialPort.isOpen()){
            byte[] reslutData = null;
            try {
                if (!serialPort.isOpen()){
                    return new byte[0];
                }
                int i=0;
                while (serialPort.bytesAvailable() > 0 && i++ < 5) {
                    Thread.sleep(20);
                }
                byte[] readBuffer = new byte[serialPort.bytesAvailable()];
                int numRead = serialPort.readBytes(readBuffer, readBuffer.length);
                if (numRead > 0) {
                    reslutData = readBuffer;
                }
            } catch (InterruptedException e) {
                log.error("读取字节数组异常",e);
            }
            return reslutData;
        }
        return new byte[0];
    }
}
