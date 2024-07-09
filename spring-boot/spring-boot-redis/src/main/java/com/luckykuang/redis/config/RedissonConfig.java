package com.luckykuang.redis.config;

import lombok.AllArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Redisson全局配置
 * @author luckykuang
 * @since 2024/6/18 12:08
 */
@Component
@AllArgsConstructor
public class RedissonConfig {

    private final RedisProperties redisProperties;

    /**
     * RedissonClient 配置
     * @return RedissonClient
     */
    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        // Linux下才能使用Epoll
//        config.setTransportMode(TransportMode.EPOLL); // 默认是NIO的方式
        // 单机模式
        config.useSingleServer().setAddress("redis://" + redisProperties.getHost() + ":" + redisProperties.getPort());
        // 集群模式，可以用"rediss://"来启用SSL连接，前缀必须是redis:// or rediss://
//        config.useClusterServers().addNodeAddress("redis://127.0.0.1:7181");
        return Redisson.create(config);
    }
}
