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

package com.luckykuang.serial.service.impl;

import com.fazecast.jSerialComm.SerialPort;
import com.luckykuang.serial.entity.SerialPortParam;
import com.luckykuang.serial.service.SerialPortService;
import com.luckykuang.serial.util.ConvertHexStrAndStrUtils;
import com.luckykuang.serial.util.SerialPortUtils;
import com.luckykuang.serial.vo.SerialPortParamVO;
import com.luckykuang.serial.vo.SerialPortStatusVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

/**
 * @author luckykuang
 * @date 2023/9/19 10:53
 */
@Slf4j
@Service
public class SerialPortServiceImpl implements SerialPortService {
    @Override
    public List<String> getSerialPortList() {
        return SerialPortUtils.getSerialPortList();
    }

    @Override
    public SerialPortParam getSerialPort(String serialPortName) {
        return SerialPortUtils.getSerialPort(serialPortName.toUpperCase());
    }

    @Override
    public String setSerialPort(SerialPortStatusVO statusVO) {
        Boolean bool = SerialPortUtils.SERIAL_PORT_STATUS.get(statusVO.getSerialPort());
        if (Boolean.TRUE.equals(bool) && Objects.equals(statusVO.getStatus(), 1)){
            log.info("串口已打开，无需再次打开");
            return "串口已打开，无需再次打开";
        }
        if ((bool == null || Boolean.FALSE.equals(bool)) && Objects.equals(statusVO.getStatus(), 0)){
            log.info("串口已关闭，无需再次关闭");
            return "串口已关闭，无需再次关闭";
        }
        SerialPortParam serialPortParam = SerialPortUtils.getSerialPort(statusVO.getSerialPort().toUpperCase());
        if (serialPortParam == null){
            log.info("该串口不存在");
            return "该串口不存在";
        }
        if (Objects.equals(statusVO.getStatus(),1)) {
            // 组装数据
            SerialPortParam param = new SerialPortParam();
            param.setSerialPort(statusVO.getSerialPort().toUpperCase());
            param.setBaudRate(statusVO.getBaudRate());
            param.setDataBit(statusVO.getDataBit());
            param.setStopBit(statusVO.getStopBit());
            param.setVerifyValue(statusVO.getVerifyValue());
            // 打开串口
            SerialPort serialPort = SerialPortUtils.openSerialPort(param);
            log.info("打开串口：{},{},{},{},{}",
                    serialPort.getSystemPortName(),
                    serialPort.getBaudRate(),
                    serialPort.getNumDataBits(),
                    serialPort.getNumStopBits(),
                    serialPort.getParity());
        } else if (Objects.equals(statusVO.getStatus(),0)) {
            // 关闭串口
            SerialPortUtils.closeSerialPort(statusVO.getSerialPort());
            log.info("关闭串口：{}",statusVO.getSerialPort());
        } else {
            return "串口状态不正确";
        }
        return "操作成功";
    }

    @Override
    public String sendData(SerialPortParamVO paramVO) {
        Boolean bool = SerialPortUtils.SERIAL_PORT_STATUS.get(paramVO.getSerialPort().toUpperCase());
        if (bool == null || Boolean.FALSE.equals(bool)){
            return "串口已关闭或不存在";
        }
        if (Objects.equals(paramVO.getType(),1)){
            log.info("发送十进制数据 串口号：{}，数据：{}",paramVO.getSerialPort(),paramVO.getData());
            SerialPortUtils.sendSerialPortData(
                    paramVO.getSerialPort().toUpperCase(),
                    paramVO.getType(),
                    Boolean.TRUE.equals(paramVO.getEnter())
                            ? (paramVO.getData() + getEnter()).getBytes(StandardCharsets.UTF_8)
                            : paramVO.getData().getBytes(StandardCharsets.UTF_8));
        }
        else if (Objects.equals(paramVO.getType(),2)){
            log.info("发送十六进制数据 串口号：{}，数据：{}",paramVO.getSerialPort(),paramVO.getData());
            SerialPortUtils.sendSerialPortData(
                    paramVO.getSerialPort().toUpperCase(),
                    paramVO.getType(),
                    Boolean.TRUE.equals(paramVO.getEnter())
                            ? ConvertHexStrAndStrUtils.hexStrToBytes(paramVO.getData() + getEnter())
                            : ConvertHexStrAndStrUtils.hexStrToBytes(paramVO.getData()));
        }
        else {
            return "数据类型错误";
        }
        return "操作成功";
    }

    private String getEnter() {
        String enter;
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            enter = "\r\n";
        } else {
            enter = "\n";
        }
        return enter;
    }
}
