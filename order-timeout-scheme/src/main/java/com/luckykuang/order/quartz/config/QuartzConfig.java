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

package com.luckykuang.order.quartz.config;

import com.luckykuang.order.quartz.service.CancelOrderQuartzService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

/**
 * @author luckykuang
 * @date 2023/7/14 11:31
 */
@Configuration
@RequiredArgsConstructor
public class QuartzConfig {

    private final CancelOrderQuartzService cancelOrderQuartzService;

    /**
     * 项目启动时加载
     */
    @PostConstruct
    public void init() {
        cancelOrderQuartzService.cancelOrder();
    }
}