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

package com.luckykuang.sse.service.impl;

import com.luckykuang.sse.service.SseService;
import com.luckykuang.sse.vo.MessageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author luckykuang
 * @date 2023/11/29 9:50
 */
@Slf4j
@Service
public class SseServiceImpl implements SseService {

    /**
     * messageId的 SseEmitter对象映射集
     */
    private static final Map<String, SseEmitter> sseEmitterMap = new ConcurrentHashMap<>();

    /**
     * 连接sse
     * @param uuid
     * @return
     */
    @Override
    public SseEmitter connect(String uuid) {
        // 超时时间设置为0，防止AsyncRequestTimeoutException超时
        SseEmitter sseEmitter = new SseEmitter(0L);

        // 连接成功需要返回数据，否则会出现待处理状态
        try {
            sseEmitter.send(SseEmitter.event().comment("welcome"));
        } catch (IOException e) {
            log.error("uuid:{},连接异常",uuid,e);
        }

        // 连接断开，会调用此完成
        sseEmitter.onCompletion(() -> {
            log.warn("uuid:{},连接断开",uuid);
            sseEmitterMap.remove(uuid);
        });

        // 连接超时
        sseEmitter.onTimeout(() -> {
            log.warn("uuid:{},连接超时",uuid);
            sseEmitterMap.remove(uuid);
        });

        // 连接报错
        sseEmitter.onError((throwable) -> {
            log.error("uuid:{},连接报错:{}",uuid,throwable.getMessage());
            sseEmitterMap.remove(uuid);
        });

        sseEmitterMap.put(uuid, sseEmitter);

        return sseEmitter;
    }

    /**
     * 发送消息
     * @param messageVO 消息体
     */
    @Override
    public void sendMessage(MessageVO messageVO) {
        messageVO.setTotal(sseEmitterMap.size());
        sseEmitterMap.forEach((uuid, sseEmitter) -> {
            try {
                log.info("uuid:{}，发送消息:{}",uuid, messageVO);
                sseEmitter.send(messageVO, MediaType.APPLICATION_JSON);
            } catch (IOException e) {
                log.warn("uuid:{},发送消息异常:{}",uuid,e.getMessage());
            }
        });
    }
}
