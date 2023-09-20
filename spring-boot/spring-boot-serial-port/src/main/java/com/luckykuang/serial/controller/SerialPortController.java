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

package com.luckykuang.serial.controller;

import com.luckykuang.serial.entity.SerialPortParam;
import com.luckykuang.serial.service.SerialPortService;
import com.luckykuang.serial.vo.SerialPortParamVO;
import com.luckykuang.serial.vo.SerialPortStatusVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author luckykuang
 * @date 2023/9/12 16:53
 */
@Tag(name = "串口 API")
@RestController
@RequestMapping("serial")
public class SerialPortController {

    @Resource
    private SerialPortService serialPortService;

    @Operation(summary = "获取所有可用串口号", description = "串口数组")
    @ApiResponse(responseCode = "200", description = "ok", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))
    })
    @ApiResponse(responseCode = "404", description = "Interface not found")
    @GetMapping("getSerialPortList")
    public List<String> getSerialPortList(){
        return serialPortService.getSerialPortList();
    }

    @Operation(summary = "通过串口号获取串口信息", description = "串口详细信息")
    @GetMapping("getSerialPort/{name}")
    public SerialPortParam getSerialPort(@PathVariable("name") String serialPortName){
        return serialPortService.getSerialPort(serialPortName);
    }

    @Operation(summary = "设置串口状态", description = "状态 1-打开 0-关闭")
    @PostMapping("setSerialPort")
    public String setSerialPort(@RequestBody @Validated SerialPortStatusVO statusVO){
        return serialPortService.setSerialPort(statusVO);
    }

    @Operation(summary = "发送数据", description = "类型 1-十进制字符串 2-十六进制字符串")
    @PostMapping("/sendData")
    public String sendData(@RequestBody @Validated SerialPortParamVO paramVO) {
        return serialPortService.sendData(paramVO);
    }
}
