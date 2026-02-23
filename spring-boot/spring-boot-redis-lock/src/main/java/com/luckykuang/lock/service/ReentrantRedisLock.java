package com.luckykuang.lock.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * 可重入Redis分布式锁
 *
 * @author LuckyKuang
 * @since 2025-01-15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReentrantRedisLock {

    private final StringRedisTemplate redisTemplate;

    private static final String LOCK_PREFIX = "lock:";
    private static final long DEFAULT_EXPIRE_TIME = 30;

    // 获取锁的Lua脚本：判断锁是否存在，是否为同一线程
    private static final String LOCK_SCRIPT =
        "local key = KEYS[1]\n" +
        "local threadId = ARGV[1]\n" +
        "local expireTime = tonumber(ARGV[2])\n" +
        "\n" +
        "if redis.call('exists', key) == 0 then\n" +
        "    -- 锁不存在，直接获取\n" +
        "    redis.call('hset', key, threadId, '1')\n" +
        "    redis.call('expire', key, expireTime)\n" +
        "    return 1\n" +
        "elseif redis.call('hexists', key, threadId) == 1 then\n" +
        "    -- 锁存在且为同一线程，重入次数+1\n" +
        "    redis.call('hincrby', key, threadId, '1')\n" +
        "    redis.call('expire', key, expireTime)\n" +
        "    return 1\n" +
        "else\n" +
        "    -- 锁被其他线程持有\n" +
        "    return 0\n" +
        "end";

    // 释放锁的Lua脚本：重入次数-1，为0时才删除
    private static final String UNLOCK_SCRIPT =
        "local key = KEYS[1]\n" +
        "local threadId = ARGV[1]\n" +
        "\n" +
        "if redis.call('hexists', key, threadId) == 0 then\n" +
        "    return 0\n" +
        "end\n" +
        "\n" +
        "local counter = tonumber(redis.call('hget', key, threadId))\n" +
        "if counter > 1 then\n" +
        "    -- 重入次数-1\n" +
        "    redis.call('hincrby', key, threadId, '-1')\n" +
        "    return 1\n" +
        "else\n" +
        "    -- 重入次数为0，删除锁\n" +
        "    redis.call('hdel', key, threadId)\n" +
        "    -- 如果哈希为空，删除key\n" +
        "    if redis.call('hlen', key) == 0 then\n" +
        "        redis.call('del', key)\n" +
        "    end\n" +
        "    return 1\n" +
        "end";

    /**
     * 释放锁
     */
    public boolean unlock(String lockKey) {
        String key = LOCK_PREFIX + lockKey;
        String threadId = getThreadId();

        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(UNLOCK_SCRIPT, Long.class);
        Long result = redisTemplate.execute(
                redisScript,
                Collections.singletonList(key),
                threadId
        );

        boolean success = result != null && result == 1L;
        if (success) {
            log.info("线程 {} 释放锁 {} -> 成功", threadId, lockKey);
        } else {
            log.warn("线程 {} 释放锁 {} -> 失败, result={}", threadId, lockKey, result);
        }

        return success;
    }

    /**
     * 获取当前线程的唯一标识
     */
    private String getThreadId() {
        return Thread.currentThread().getName() + ":" + Thread.currentThread().getId();
    }

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
        String key = LOCK_PREFIX + lockKey;
        String threadId = getThreadId();
        long expireSeconds = unit.toSeconds(expireTime);

        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(LOCK_SCRIPT, Long.class);
        Long result = redisTemplate.execute(
                redisScript,
                Collections.singletonList(key),
                threadId,
                String.valueOf(expireSeconds)
        );

        boolean success = result != null && result == 1L;
        if (success) {
            log.info("线程 {} 尝试获取锁 {} -> 成功", threadId, lockKey);
        } else {
            log.warn("线程 {} 尝试获取锁 {} -> 失败", threadId, lockKey);
        }

        return success;
    }

    /**
     * 带锁执行
     */
    public void executeWithLock(String lockKey, Runnable runnable) {
        if (!tryLock(lockKey)) {
            throw new RuntimeException("获取锁失败");
        }
        try {
            runnable.run();
        } finally {
            unlock(lockKey);
        }
    }

    /**
     * 检查锁是否存在（用于调试）
     */
    public boolean checkLockExists(String lockKey) {
        String key = LOCK_PREFIX + lockKey;
        Boolean exists = redisTemplate.hasKey(key);
        return Boolean.TRUE.equals(exists);
    }

    /**
     * 删除锁（强制删除，用于调试）
     */
    public void forceDeleteLock(String lockKey) {
        String key = LOCK_PREFIX + lockKey;
        redisTemplate.delete(key);
    }
}
