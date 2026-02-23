package com.luckykuang.memcached.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rubyeye.xmemcached.MemcachedClient;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Memcached基础版分布式锁
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BasicMemcachedLock {

    private final MemcachedClient memcachedClient;

    private static final String LOCK_PREFIX = "lock:";
    private static final int DEFAULT_EXPIRE_TIME = 30; // 默认30秒

    /**
     * 尝试获取锁
     * @param lockKey 锁的key
     * @return 是否获取成功
     */
    public boolean tryLock(String lockKey) {
        return tryLock(lockKey, DEFAULT_EXPIRE_TIME);
    }

    /**
     * 尝试获取锁（带过期时间）
     * @param lockKey 锁的key
     * @param expireTime 过期时间（秒）
     * @return 是否获取成功
     */
    public boolean tryLock(String lockKey, int expireTime) {
        String key = LOCK_PREFIX + lockKey;
        String value = UUID.randomUUID().toString();

        try {
            // 使用ADD命令：仅当key不存在时添加
            boolean added = memcachedClient.add(key, expireTime, value);

            if (added) {
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
     * @param lockKey 锁的key
     */
    public void unlock(String lockKey) {
        String key = LOCK_PREFIX + lockKey;

        try {
            boolean deleted = memcachedClient.delete(key);
            log.info("释放锁: {} -> {}", key, deleted ? "成功" : "失败");
        } catch (Exception e) {
            log.error("释放锁异常: {}", key, e);
        }
    }

    /**
     * 带锁执行的方法
     * @param lockKey 锁的key
     * @param runnable 要执行的逻辑
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
