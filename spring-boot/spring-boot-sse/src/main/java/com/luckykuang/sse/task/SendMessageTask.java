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

package com.luckykuang.sse.task;

import com.luckykuang.sse.service.SseService;
import com.luckykuang.sse.vo.MessageVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author luckykuang
 * @date 2023/11/29 9:52
 */
@Slf4j
@Configuration
@EnableScheduling
public class SendMessageTask {

    @Resource
    private SseService sseService;

    /**
     * 定时执行 秒 分 时 日 月 周
     */
    @Scheduled(cron = "*/5 * * * * *")  // 间隔5秒
    public void sendMessageTask() {
        MessageVO messageVO = new MessageVO();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        messageVO.setData(LocalDateTime.now().format(format));
        sseService.sendMessage(messageVO);
    }
}
