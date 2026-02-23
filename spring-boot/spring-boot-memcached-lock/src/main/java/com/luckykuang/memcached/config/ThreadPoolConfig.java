package com.luckykuang.memcached.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程池配置（用于自动续期）
 */
@Configuration
public class ThreadPoolConfig {

    @Bean("lockScheduledExecutor")
    public ScheduledExecutorService lockScheduledExecutor() {
        return Executors.newScheduledThreadPool(4, new NamedThreadFactory("lock-renewal-"));
    }

    /**
     * 命名线程工厂
     */
    static class NamedThreadFactory implements ThreadFactory {
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        NamedThreadFactory(String namePrefix) {
            this.namePrefix = namePrefix;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, namePrefix + threadNumber.getAndIncrement());
            thread.setDaemon(false);
            return thread;
        }
    }
}
