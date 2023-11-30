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

package com.luckykuang.sse.controller;

import com.luckykuang.sse.service.SseService;
import com.luckykuang.sse.vo.MessageVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

/**
 * @author luckykuang
 * @date 2023/11/29 9:52
 */
@Slf4j
@Controller
@RequestMapping("sse")
public class SseController {

    @Resource
    private SseService sseService;

    /**
     * 首页
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }

    /**
     * 创建SSE连接
     * 请求类型一定要是 MediaType.TEXT_EVENT_STREAM_VALUE
     */
    @GetMapping(path = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter sse() {
        String uuid = UUID.randomUUID().toString();
        log.info("新用户连接：{}", uuid);
        return sseService.connect(uuid);
    }

    /**
     * 广播消息
     */
    @PostMapping("/sendMessage")
    @ResponseBody
    public void sendMessage(@RequestBody MessageVO messageVO) {
        sseService.sendMessage(messageVO);
    }
}
