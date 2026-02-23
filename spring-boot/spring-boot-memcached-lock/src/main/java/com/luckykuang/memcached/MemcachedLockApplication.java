package com.luckykuang.memcached;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Memcached分布式锁应用启动类
 */
@SpringBootApplication
public class MemcachedLockApplication {

    public static void main(String[] args) {
        SpringApplication.run(MemcachedLockApplication.class, args);
    }
}
