package com.luckykuang.lock.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 基础版Redis分布式锁
 *
 * @author LuckyKuang
 * @since 2025-01-15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BasicRedisLock {

    private final StringRedisTemplate redisTemplate;

    private static final String LOCK_PREFIX = "lock:";
    private static final long DEFAULT_EXPIRE_TIME = 30; // 默认30秒

    /**
     * 尝试获取锁
     * @param lockKey 锁的key
     * @return 是否获取成功
     */
    public boolean tryLock(String lockKey) {
        return tryLock(lockKey, DEFAULT_EXPIRE_TIME, TimeUnit.SECONDS);
    }

    /**
     * 尝试获取锁（带过期时间）
     * @param lockKey 锁的key
     * @param expireTime 过期时间
     * @param unit 时间单位
     * @return 是否获取成功
     */
    public boolean tryLock(String lockKey, long expireTime, TimeUnit unit) {
        String value = UUID.randomUUID().toString();
        String key = LOCK_PREFIX + lockKey;

        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(key, value, expireTime, unit);

        log.info("尝试获取锁 {} -> {}", key, Boolean.TRUE.equals(success) ? "成功" : "失败");
        return Boolean.TRUE.equals(success);
    }

    /**
     * 释放锁
     * @param lockKey 锁的key
     */
    public void unlock(String lockKey) {
        String key = LOCK_PREFIX + lockKey;
        redisTemplate.delete(key);
        log.info("释放锁 {}", key);
    }

    /**
     * 带锁执行的方法
     * @param lockKey 锁的key
     * @param runnable 要执行的逻辑
     */
    public void executeWithLock(String lockKey, Runnable runnable) {
        if (tryLock(lockKey)) {
            try {
                runnable.run();
            } finally {
                unlock(lockKey);
            }
        } else {
            log.warn("获取锁失败，无法执行业务逻辑");
            throw new RuntimeException("获取锁失败");
        }
    }
}
