package com.luckykuang.memcached.config;

import lombok.Data;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.impl.KetamaMemcachedSessionLocator;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * Memcached配置类
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "memcached")
public class MemcachedConfig {

    private String servers;
    private int poolSize;
    private long connectionTimeout;
    private long operationTimeout;
    private boolean useNagle;

    /**
     * 创建Memcached客户端
     */
    @Bean(destroyMethod = "shutdown")
    public MemcachedClient memcachedClient() throws IOException {
        MemcachedClientBuilder builder = new XMemcachedClientBuilder(
                getAddressList()
        );

        // 简化配置 - 不使用连接池
        builder.setConnectionPoolSize(1);
        builder.setConnectTimeout(5000);
        builder.setOpTimeout(operationTimeout);

        // 使用一致性哈希
        builder.setSessionLocator(new KetamaMemcachedSessionLocator());

        MemcachedClient client = builder.build();
        System.out.println("Memcached客户端启动成功！连接服务器: " + servers);

        return client;
    }

    private String getAddressList() {
        return servers;
    }
}
