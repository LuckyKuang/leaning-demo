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

package com.luckykuang.mqtt.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * mqtt客户端配置
 * @author luckykuang
 * @date 2023/8/9 17:42
 */
@Data
@Configuration
@ConfigurationProperties(prefix = MqttConfig.PREFIX)
public class MqttConfig {
    // 指定前缀
    public static final String PREFIX = "publish.mqtt";
    private String host;
    private String clientId;
    private String username;
    private String password;
    // 持久化订阅(false)：重连时不需要再次订阅
    // 非持久化订阅(true)：重连时需要再次订阅
    private boolean cleanSession;
    private String defaultTopic;
    private int timeout;
    private int keepalive;
    private int connectionTimeout;
}
