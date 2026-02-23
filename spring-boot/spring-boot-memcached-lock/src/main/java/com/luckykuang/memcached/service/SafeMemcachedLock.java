package com.luckykuang.memcached.service;

import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rubyeye.xmemcached.MemcachedClient;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Memcached防误删版分布式锁
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SafeMemcachedLock {

    private final MemcachedClient memcachedClient;

    private static final String LOCK_PREFIX = "lock:";
    private static final int DEFAULT_EXPIRE_TIME = 30;

    /**
     * 尝试获取锁
     */
    public boolean tryLock(String lockKey) {
        return tryLock(lockKey, DEFAULT_EXPIRE_TIME);
    }

    /**
     * 尝试获取锁（带过期时间）
     */
    public boolean tryLock(String lockKey, int expireTime) {
        String key = LOCK_PREFIX + lockKey;
        String value = UUID.randomUUID().toString();

        try {
            boolean added = memcachedClient.add(key, expireTime, value);

            if (added) {
                // 将锁标识保存到ThreadLocal
                LockHolder.setLockValue(key, value);
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
     * 释放锁（带防误删检查）
     */
    public boolean unlock(String lockKey) {
        String key = LOCK_PREFIX + lockKey;
        String expectedValue = LockHolder.getLockValue(key);

        if (StrUtil.isBlank(expectedValue)) {
            log.warn("锁 {} 不存在或已过期", key);
            return false;
        }

        try {
            // 获取当前锁的值
            String actualValue = memcachedClient.get(key);

            // 比较值是否匹配
            if (expectedValue.equals(actualValue)) {
                boolean deleted = memcachedClient.delete(key);
                LockHolder.removeLockValue(key);
                log.info("释放锁成功: {}", key);
                return deleted;
            } else {
                log.warn("释放锁失败: {} (值不匹配, 可能已过期)", key);
                LockHolder.removeLockValue(key);
                return false;
            }
        } catch (Exception e) {
            log.error("释放锁异常: {}", key, e);
            return false;
        }
    }

    /**
     * 带锁执行的方法
     */
    public boolean executeWithLock(String lockKey, Runnable runnable) {
        return executeWithLock(lockKey, DEFAULT_EXPIRE_TIME, runnable);
    }

    /**
     * 带锁执行的方法（带超时时间）
     */
    public boolean executeWithLock(String lockKey, int expireTime, Runnable runnable) {
        if (!tryLock(lockKey, expireTime)) {
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
    private static final ThreadLocal<java.util.HashMap<String, String>> LOCK_MAP =
            ThreadLocal.withInitial(java.util.HashMap::new);

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
