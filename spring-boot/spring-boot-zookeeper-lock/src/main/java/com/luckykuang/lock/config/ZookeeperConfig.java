package com.luckykuang.lock.config;

import lombok.Data;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Zookeeper配置类
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "zookeeper")
public class ZookeeperConfig {

    private String connectString;
    private int sessionTimeout;
    private int connectionTimeout;
    private int baseSleepTime;
    private int maxRetries;

    /**
     * 创建Curator客户端
     */
    @Bean(destroyMethod = "close")
    public CuratorFramework curatorFramework() {
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(
                baseSleepTime, maxRetries
        );

        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(connectString)
                .sessionTimeoutMs(sessionTimeout)
                .connectionTimeoutMs(connectionTimeout)
                .retryPolicy(retryPolicy)
                .namespace("distributed-lock")
                .build();

        client.start();
        System.out.println("Zookeeper客户端启动成功！");
        return client;
    }
}
