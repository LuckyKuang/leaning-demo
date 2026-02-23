package com.luckykuang.memcached.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * Memcached分布式锁注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface McLock {

    /**
     * 锁的key，支持SpEL表达式
     * 例如："order:#{#orderId}"
     */
    String key();

    /**
     * 锁的前缀
     */
    String prefix() default "lock:";

    /**
     * 过期时间
     */
    long expireTime() default 30;

    /**
     * 时间单位
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 获取锁失败时的行为
     */
    LockFailStrategy failStrategy() default LockFailStrategy.THROW_EXCEPTION;

    /**
     * 是否自动续期
     */
    boolean autoRenewal() default false;

    /**
     * 获取锁失败策略枚举
     */
    enum LockFailStrategy {
        /** 抛出异常 */
        THROW_EXCEPTION,
        /** 快速失败，返回null */
        FAIL_FAST,
        /** 忽略，继续执行 */
        IGNORE
    }
}
