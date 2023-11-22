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

package com.luckykuang.tcp.controller;

import com.luckykuang.tcp.service.TcpClientService;
import com.luckykuang.tcp.vo.ConnectVO;
import com.luckykuang.tcp.vo.SendVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * tcp client 控制层
 * @author luckykuang
 * @date 2023/11/21 16:01
 */
@RestController
@RequestMapping("tcp/client")
@RequiredArgsConstructor
public class TcpClientController {

    private final TcpClientService tcpClientService;

    /**
     * 网络连接
     * @param vo
     * @return
     */
    @PostMapping("connect")
    public ResponseEntity<String> connect(@RequestBody ConnectVO vo){
        return tcpClientService.connect(vo);
    }

    /**
     * 网络连接成功后发送指令
     * @param vo
     * @return
     */
    @PostMapping("send")
    public ResponseEntity<String> send(@RequestBody SendVO vo){
        return tcpClientService.send(vo);
    }
}
