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

package com.luckykuang.rocketmq.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * RocketMq 环境隔离配置
 * @author luckykuang
 * @date 2023/10/22 22:51
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "rocketmq.enhance")
public class RocketMqProperties {
    // 是否启用隔离
    private boolean enabledIsolation;
    // 隔离环境名称(dev、test、prod)
    private String environment;
}
