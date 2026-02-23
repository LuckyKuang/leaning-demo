package com.luckykuang.memcached.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rubyeye.xmemcached.MemcachedClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Memcached可重入分布式锁
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReentrantMemcachedLock {

    private final MemcachedClient memcachedClient;

    private static final String LOCK_PREFIX = "lock:";
    private static final String DELIMITER = ":";
    private static final int DEFAULT_EXPIRE_TIME = 30;

    /** 每个线程的重入计数 */
    private final ThreadLocal<java.util.HashMap<String, AtomicInteger>> reentrantCountMap =
            ThreadLocal.withInitial(java.util.HashMap::new);

    /**
     * 尝试获取锁
     */
    public boolean tryLock(String lockKey) {
        return tryLock(lockKey, DEFAULT_EXPIRE_TIME);
    }

    /**
     * 尝试获取锁（带过期时间）
     */
    public synchronized boolean tryLock(String lockKey, int expireTime) {
        String key = LOCK_PREFIX + lockKey;
        String threadId = getThreadId();

        try {
            // 1. 检查是否已持有锁（可重入）
            String currentValue = memcachedClient.get(key);

            if (currentValue != null && currentValue.startsWith(threadId)) {
                // 已持有锁，增加重入计数
                String[] parts = currentValue.split(DELIMITER);
                int count = Integer.parseInt(parts[1]) + 1;
                String newValue = threadId + DELIMITER + count;

                memcachedClient.set(key, expireTime, newValue);
                incrementReentrantCount(key);
                log.info("锁重入成功: {} (重入次数: {})", key, count);
                return true;
            }

            // 2. 未持有锁，尝试获取
            String value = threadId + DELIMITER + "1";
            boolean added = memcachedClient.add(key, expireTime, value);

            if (added) {
                incrementReentrantCount(key);
                log.info("获取锁成功: {} -> {}", key, value);
            } else {
                log.info("获取锁失败: {} (锁已被占用)", key);
            }

            return added;
        } catch (Exception e) {
            log.error("获取锁异常: {}", key, e);
            return false;
        }
    }

    /**
     * 释放锁
     */
    public synchronized boolean unlock(String lockKey) {
        String key = LOCK_PREFIX + lockKey;
        String threadId = getThreadId();

        try {
            String currentValue = memcachedClient.get(key);

            // 检查锁是否属于当前线程
            if (currentValue == null || !currentValue.startsWith(threadId)) {
                log.warn("释放锁失败: {} (锁不属于当前线程)", key);
                return false;
            }

            // 解析重入计数
            String[] parts = currentValue.split(DELIMITER);
            int count = Integer.parseInt(parts[1]);

            if (count > 1) {
                // 重入次数 > 1，减少计数
                String newValue = threadId + DELIMITER + (count - 1);
                memcachedClient.set(key, DEFAULT_EXPIRE_TIME, newValue);
                decrementReentrantCount(key);
                log.info("减少锁计数: {} (剩余: {})", key, count - 1);
                return true;
            } else {
                // 重入次数 = 1，完全释放锁
                boolean deleted = memcachedClient.delete(key);
                removeReentrantCount(key);
                log.info("释放锁成功: {}", key);
                return deleted;
            }
        } catch (Exception e) {
            log.error("释放锁异常: {}", key, e);
            return false;
        }
    }

    /**
     * 获取当前线程的唯一标识
     */
    private String getThreadId() {
        return Thread.currentThread().getName() + "-" + Thread.currentThread().getId();
    }

    /**
     * 增加重入计数
     */
    private void incrementReentrantCount(String key) {
        java.util.HashMap<String, AtomicInteger> map = reentrantCountMap.get();
        map.computeIfAbsent(key, k -> new AtomicInteger(0)).incrementAndGet();
    }

    /**
     * 减少重入计数
     */
    private void decrementReentrantCount(String key) {
        java.util.HashMap<String, AtomicInteger> map = reentrantCountMap.get();
        AtomicInteger counter = map.get(key);
        if (counter != null) {
            if (counter.decrementAndGet() <= 0) {
                map.remove(key);
                if (map.isEmpty()) {
                    reentrantCountMap.remove();
                }
            }
        }
    }

    /**
     * 移除重入计数
     */
    private void removeReentrantCount(String key) {
        java.util.HashMap<String, AtomicInteger> map = reentrantCountMap.get();
        if (map != null) {
            map.remove(key);
            if (map.isEmpty()) {
                reentrantCountMap.remove();
            }
        }
    }

    /**
     * 带锁执行的方法
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
}
