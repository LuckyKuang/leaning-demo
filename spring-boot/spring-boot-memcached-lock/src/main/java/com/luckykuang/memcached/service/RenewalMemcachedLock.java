package com.luckykuang.memcached.service;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rubyeye.xmemcached.MemcachedClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Memcached自动续期分布式锁
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RenewalMemcachedLock {

    private final MemcachedClient memcachedClient;

    @Qualifier("lockScheduledExecutor")
    private final ScheduledExecutorService scheduledExecutor;

    private static final String LOCK_PREFIX = "lock:";
    private static final int DEFAULT_EXPIRE_TIME = 30;
    private static final int RENEWAL_INTERVAL = 3; // 每3秒续期一次（需要在过期时间内执行）

    /** 存储续期任务 */
    private final ConcurrentHashMap<String, ScheduledFuture<?>> renewalTasks = new ConcurrentHashMap<>();

    /** 锁标识与线程ID的映射 */
    private final ConcurrentHashMap<String, String> lockIdentifiers = new ConcurrentHashMap<>();

    /**
     * 尝试获取锁
     */
    public boolean tryLock(String lockKey) {
        return tryLock(lockKey, DEFAULT_EXPIRE_TIME);
    }

    public boolean tryLock(String lockKey, int expireTime) {
        String key = LOCK_PREFIX + lockKey;
        String value = UUID.randomUUID().toString();

        try {
            boolean added = memcachedClient.add(key, expireTime, value);

            if (added) {
                lockIdentifiers.put(key, value);
                // 启动自动续期任务
                startRenewalTask(key, value, expireTime);
                log.info("获取锁成功并启动续期: {}", key);
            } else {
                log.info("获取锁失败: {}", key);
            }

            return added;
        } catch (Exception e) {
            log.error("获取锁异常: {}", key, e);
            return false;
        }
    }

    /**
     * 启动续期任务（WatchDog）
     */
    private void startRenewalTask(String key, String value, int expireTime) {
        // 计算续期间隔：应该是过期时间的1/3，确保至少能续期2次
        long renewalInterval = Math.min(RENEWAL_INTERVAL, expireTime / 3);
        if (renewalInterval < 1) {
            renewalInterval = 1; // 最小1秒
        }

        final long interval = renewalInterval;
        ScheduledFuture<?> task = scheduledExecutor.scheduleAtFixedRate(() -> {
            try {
                // 检查锁是否还存在
                String currentValue = memcachedClient.get(key);

                if (value.equals(currentValue)) {
                    // 延长过期时间
                    boolean updated = memcachedClient.set(key, expireTime, value);
                    if (updated) {
                        log.debug("锁 {} 续期成功，延长{}秒", key, expireTime);
                    } else {
                        // 续期失败，停止续期任务
                        stopRenewalTask(key);
                        log.warn("锁 {} 续期失败，停止续期任务", key);
                    }
                } else {
                    // 锁已被其他线程持有或已释放
                    stopRenewalTask(key);
                    log.warn("锁 {} 已失效，停止续期任务", key);
                }
            } catch (Exception e) {
                log.error("续期任务执行异常: {}", key, e);
                // 异常时停止续期任务，避免继续执行
                stopRenewalTask(key);
            }
        }, interval, interval, TimeUnit.SECONDS);

        renewalTasks.put(key, task);
        log.info("启动续期任务: {}, 间隔{}秒", key, interval);
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
        lockIdentifiers.remove(key);
    }

    /**
     * 释放锁
     */
    public boolean unlock(String lockKey) {
        String key = LOCK_PREFIX + lockKey;
        String expectedValue = lockIdentifiers.get(key);

        if (expectedValue == null) {
            log.warn("锁 {} 不存在", key);
            return false;
        }

        try {
            // 验证锁的值
            String currentValue = memcachedClient.get(key);
            if (expectedValue.equals(currentValue)) {
                boolean deleted = memcachedClient.delete(key);
                stopRenewalTask(key);
                log.info("释放锁成功: {}", key);
                return deleted;
            } else {
                log.warn("释放锁失败: {} (值不匹配)", key);
                stopRenewalTask(key);
                return false;
            }
        } catch (Exception e) {
            log.error("释放锁异常: {}", key, e);
            stopRenewalTask(key);
            return false;
        }
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
        lockIdentifiers.clear();
        log.info("清理所有续期任务");
    }
}
