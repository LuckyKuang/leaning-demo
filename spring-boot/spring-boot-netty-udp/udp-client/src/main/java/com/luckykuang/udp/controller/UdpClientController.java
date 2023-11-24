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

package com.luckykuang.udp.controller;

import com.luckykuang.udp.service.UdpClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author luckykuang
 * @date 2023/11/3 18:42
 */
@RestController
@RequestMapping("udp")
@RequiredArgsConstructor
public class UdpClientController {

    private final UdpClientService udpClientService;

    /**
     * 发送udp消息
     * @param ip    地址 比如：单播(192.168.1.100) 广播(192.168.1.255)
     * @param port  端口
     * @param data  数据
     * @param codec 编码 十进制-ascii 十六进制-hex
     * @return
     */
    @GetMapping("send")
    public String send(String ip,Integer port,String data,String codec){
        return udpClientService.send(ip,port,data,codec);
    }
}
