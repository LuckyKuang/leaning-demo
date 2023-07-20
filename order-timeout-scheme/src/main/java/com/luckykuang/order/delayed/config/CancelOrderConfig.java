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

package com.luckykuang.order.delayed.config;

import com.luckykuang.order.delayed.service.CancelOrderDelayedService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

/**
 * @author luckykuang
 * @date 2023/7/14 14:45
 */
@Configuration
@RequiredArgsConstructor
public class CancelOrderConfig {

    private final CancelOrderDelayedService cancelOrderDelayedService;

    /**
     * 项目启动时加载
     */
    @PostConstruct
    public void init() {
        cancelOrderDelayedService.cancelOrder();
    }
}
