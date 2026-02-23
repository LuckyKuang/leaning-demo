package com.luckykuang.lock.service;

import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 防误删版Redis分布式锁
 *
 * @author LuckyKuang
 * @since 2025-01-15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SafeRedisLock {

    private final StringRedisTemplate redisTemplate;

    private static final String LOCK_PREFIX = "lock:";
    private static final long DEFAULT_EXPIRE_TIME = 30;

    // Lua脚本：保证判断和删除的原子性
    private static final String UNLOCK_SCRIPT =
        "if redis.call('get', KEYS[1]) == ARGV[1] then " +
        "    return redis.call('del', KEYS[1]) " +
        "else " +
        "    return 0 " +
        "end";

    /**
     * 尝试获取锁
     */
    public boolean tryLock(String lockKey) {
        return tryLock(lockKey, DEFAULT_EXPIRE_TIME, TimeUnit.SECONDS);
    }

    /**
     * 尝试获取锁（带过期时间）
     */
    public boolean tryLock(String lockKey, long expireTime, TimeUnit unit) {
        String value = UUID.randomUUID().toString();
        String key = LOCK_PREFIX + lockKey;

        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(key, value, expireTime, unit);

        if (Boolean.TRUE.equals(success)) {
            // 将锁标识保存到ThreadLocal，用于释放时判断
            LockHolder.setLockValue(key, value);
        }

        return Boolean.TRUE.equals(success);
    }

    /**
     * 释放锁（使用Lua脚本保证原子性）
     */
    public boolean unlock(String lockKey) {
        String key = LOCK_PREFIX + lockKey;
        String value = LockHolder.getLockValue(key);

        if (StrUtil.isBlank(value)) {
            log.warn("锁 {} 不存在或已过期", key);
            return false;
        }

        // 执行Lua脚本
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(UNLOCK_SCRIPT, Long.class);
        Long result = redisTemplate.execute(
                redisScript,
                Collections.singletonList(key),
                value
        );

        boolean success = result != null && result == 1L;
        if (success) {
            LockHolder.removeLockValue(key);
        }

        log.info("释放锁 {} -> {}", key, success ? "成功" : "失败");
        return success;
    }

    /**
     * 带锁执行的方法
     */
    public boolean executeWithLock(String lockKey, Runnable runnable) {
        return executeWithLock(lockKey, DEFAULT_EXPIRE_TIME, TimeUnit.SECONDS, runnable);
    }

    /**
     * 带锁执行的方法（带超时时间）
     */
    public boolean executeWithLock(String lockKey, long expireTime, TimeUnit unit, Runnable runnable) {
        if (!tryLock(lockKey, expireTime, unit)) {
            log.warn("获取锁 {} 失败", lockKey);
            return false;
        }

        try {
            runnable.run();
            return true;
        } finally {
            unlock(lockKey);
        }
    }
}

/**
 * 锁标识持有者（ThreadLocal）
 */
class LockHolder {
    private static final ThreadLocal<java.util.Map<String, String>> LOCK_MAP = ThreadLocal.withInitial(java.util.HashMap::new);

    static void setLockValue(String key, String value) {
        LOCK_MAP.get().put(key, value);
    }

    static String getLockValue(String key) {
        return LOCK_MAP.get().get(key);
    }

    static void removeLockValue(String key) {
        LOCK_MAP.get().remove(key);
        if (LOCK_MAP.get().isEmpty()) {
            LOCK_MAP.remove();
        }
    }
}
