package com.luckykuang.lock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Zookeeper分布式锁启动类
 */
@SpringBootApplication
public class ZookeeperLockApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZookeeperLockApplication.class, args);
    }
}
