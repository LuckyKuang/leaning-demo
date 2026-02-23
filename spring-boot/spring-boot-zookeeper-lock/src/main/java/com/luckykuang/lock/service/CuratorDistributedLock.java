package com.luckykuang.lock.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Curator分布式锁封装
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CuratorDistributedLock {

    private final CuratorFramework curatorFramework;

    private static final String LOCK_PATH = "/locks";

    /** 锁实例缓存 */
    private final ConcurrentHashMap<String, InterProcessMutex> lockMap = new ConcurrentHashMap<>();

    /** 本地锁（防止重复创建InterProcessMutex） */
    private final ReentrantLock localLock = new ReentrantLock();

    /**
     * 获取锁实例
     */
    private InterProcessMutex getLock(String lockKey) {
        return lockMap.computeIfAbsent(lockKey, key -> {
            String path = LOCK_PATH + "/" + key;
            return new InterProcessMutex(curatorFramework, path);
        });
    }

    /**
     * 尝试获取锁
     */
    public boolean tryLock(String lockKey) {
        return tryLock(lockKey, 60, TimeUnit.SECONDS);
    }

    /**
     * 尝试获取锁（带超时时间）
     */
    public boolean tryLock(String lockKey, long timeout, TimeUnit unit) {
        InterProcessMutex lock = getLock(lockKey);

        try {
            boolean acquired = lock.acquire(timeout, unit);
            if (acquired) {
                log.info("获取锁成功: {}", lockKey);
            } else {
                log.warn("获取锁超时: {}", lockKey);
            }
            return acquired;
        } catch (Exception e) {
            log.error("获取锁失败: {}", lockKey, e);
            return false;
        }
    }

    /**
     * 释放锁
     */
    public void unlock(String lockKey) {
        InterProcessMutex lock = getLock(lockKey);

        try {
            if (lock.isAcquiredInThisProcess()) {
                lock.release();
                log.info("释放锁成功: {}", lockKey);
            } else {
                log.warn("锁未持有: {}", lockKey);
            }
        } catch (Exception e) {
            log.error("释放锁失败: {}", lockKey, e);
        }
    }

    /**
     * 带锁执行的方法
     */
    public boolean executeWithLock(String lockKey, Runnable runnable) {
        return executeWithLock(lockKey, 60, TimeUnit.SECONDS, runnable);
    }

    /**
     * 带锁执行的方法（带超时时间）
     */
    public boolean executeWithLock(String lockKey, long timeout, TimeUnit unit, Runnable runnable) {
        if (!tryLock(lockKey, timeout, unit)) {
            log.warn("获取锁失败，无法执行业务逻辑");
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
