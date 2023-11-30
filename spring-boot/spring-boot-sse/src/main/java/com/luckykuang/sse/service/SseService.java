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

package com.luckykuang.sse.service;

import com.luckykuang.sse.vo.MessageVO;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * @author luckykuang
 * @date 2023/11/29 9:49
 */
public interface SseService {
    SseEmitter connect(String uuid);

    void sendMessage(MessageVO messageVO);
}
