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

package com.luckykuang.netty.service.impl;

import com.luckykuang.netty.service.SendMsgService;
import com.luckykuang.netty.util.WebsocketChannelUtils;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author luckykuang
 * @date 2023/8/22 17:19
 */
@Slf4j
@Service
public class SendMsgServiceImpl implements SendMsgService {
    @Override
    public void sendMsgById(String id, String msg) {
        TextWebSocketFrame tws = new TextWebSocketFrame(msg);
        WebsocketChannelUtils.sendMsgById(id,tws);
        log.info("单发完成 id:{},msg:{}",id ,msg);
    }

    @Override
    public void sendMsgToAll(String msg) {
        TextWebSocketFrame tws = new TextWebSocketFrame(msg);
        WebsocketChannelUtils.sendMsgToAll(tws);
        log.info("群发完成 msg:{}",msg);
    }
}
