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

package com.luckykuang.netty.controller;

import com.luckykuang.netty.service.SendMsgService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * websocket测试
 * @author luckykuang
 * @date 2023/8/22 17:18
 */
@RestController
@RequestMapping("test")
@RequiredArgsConstructor
public class TestController {

    private final SendMsgService sendMsgService;

    /**
     * 服务端向指定客户端发送消息
     * @param id
     * @param msg
     */
    @GetMapping("sendMsgById")
    public void sendMsgById(String id, String msg){
        sendMsgService.sendMsgById(id,msg);
    }

    /**
     * 服务端向所有客户端发送消息
     * @param msg
     */
    @GetMapping("sendMsgToAll")
    public void sendMsgToAll(String msg){
        sendMsgService.sendMsgToAll(msg);
    }
}
