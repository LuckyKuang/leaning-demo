package com.luckykuang.lock.service;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.concurrent.*;

/**
 * 自动续期Redis分布式锁（WatchDog机制）
 *
 * @author LuckyKuang
 * @since 2025-01-15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RenewalRedisLock {

    private final StringRedisTemplate redisTemplate;
    private final ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(4);

    private static final String LOCK_PREFIX = "lock:";
    private static final long DEFAULT_EXPIRE_TIME = 30;
    private static final long RENEWAL_INTERVAL = 10; // 每10秒续期一次

    // 存储续期任务
    private final ConcurrentHashMap<String, ScheduledFuture<?>> renewalTasks = new ConcurrentHashMap<>();

    // 续期Lua脚本
    private static final String RENEWAL_SCRIPT =
        "if redis.call('hexists', KEYS[1], ARGV[1]) == 1 then\n" +
        "    return redis.call('expire', KEYS[1], ARGV[2])\n" +
        "else\n" +
        "    return 0\n" +
        "end";

    private String getThreadId() {
        return Thread.currentThread().getName() + ":" + Thread.currentThread().getId();
    }

    /**
     * 尝试获取锁并启动自动续期
     */
    public boolean tryLock(String lockKey) {
        return tryLock(lockKey, DEFAULT_EXPIRE_TIME, TimeUnit.SECONDS);
    }

    public boolean tryLock(String lockKey, long expireTime, TimeUnit unit) {
        String key = LOCK_PREFIX + lockKey;
        String threadId = getThreadId();
        long expireSeconds = unit.toSeconds(expireTime);

        // 使用可重入锁的逻辑获取锁
        Boolean success = redisTemplate.opsForValue().setIfAbsent(
                key, threadId, expireTime, unit
        );

        if (Boolean.TRUE.equals(success)) {
            // 启动自动续期任务
            startRenewalTask(key, threadId, expireSeconds);
            log.info("获取锁 {} 成功，启动自动续期", lockKey);
            return true;
        }

        return false;
    }

    /**
     * 启动续期任务（WatchDog）
     */
    private void startRenewalTask(String key, String threadId, long expireSeconds) {
        ScheduledFuture<?> task = scheduledExecutor.scheduleAtFixedRate(() -> {
            try {
                DefaultRedisScript<Long> script = new DefaultRedisScript<>(RENEWAL_SCRIPT, Long.class);
                Long result = redisTemplate.execute(
                        script,
                        Collections.singletonList(key),
                        threadId,
                        String.valueOf(expireSeconds)
                );

                if (result != null && result == 1L) {
                    log.debug("锁 {} 续期成功，延长{}秒", key, expireSeconds);
                } else {
                    // 续期失败，锁可能已释放，停止续期任务
                    stopRenewalTask(key);
                    log.warn("锁 {} 续期失败，停止续期任务", key);
                }
            } catch (Exception e) {
                log.error("续期任务执行异常", e);
            }
        }, RENEWAL_INTERVAL, RENEWAL_INTERVAL, TimeUnit.SECONDS);

        renewalTasks.put(key, task);
    }

    /**
     * 停止续期任务
     */
    private void stopRenewalTask(String key) {
        ScheduledFuture<?> task = renewalTasks.remove(key);
        if (task != null && !task.isCancelled()) {
            task.cancel(false);
            log.info("停止锁 {} 的续期任务", key);
        }
    }

    /**
     * 释放锁
     */
    public boolean unlock(String lockKey) {
        String key = LOCK_PREFIX + lockKey;
        String threadId = getThreadId();

        // 停止续期任务
        stopRenewalTask(key);

        // 释放锁
        String value = redisTemplate.opsForValue().get(key);
        if (threadId.equals(value)) {
            redisTemplate.delete(key);
            log.info("释放锁 {} 成功", lockKey);
            return true;
        }

        log.warn("释放锁 {} 失败，锁不属于当前线程", lockKey);
        return false;
    }

    /**
     * 应用关闭时清理所有续期任务
     */
    @PreDestroy
    public void destroy() {
        renewalTasks.forEach((key, task) -> {
            if (!task.isCancelled()) {
                task.cancel(false);
            }
        });
        renewalTasks.clear();
        scheduledExecutor.shutdown();
        log.info("清理所有续期任务");
    }
}
