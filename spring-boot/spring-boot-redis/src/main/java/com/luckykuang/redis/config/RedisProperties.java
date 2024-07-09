package com.luckykuang.redis.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * redis配置读取
 * @author luckykuang
 * @since 2024/6/18 12:02
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "spring.data.redis")
public class RedisProperties {
    private int database;
    private String host;
    private int port;
    private String password;
}
