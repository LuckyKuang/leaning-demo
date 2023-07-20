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

package com.luckykuang.order.quartz.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author luckykuang
 * @date 2023/7/14 10:15
 */
@Slf4j
public class CancelOrderJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        log.info(time + ":扫描订单表超时未付款订单...");
        // TODO 这里写订单超时处理的业务逻辑
    }
}
