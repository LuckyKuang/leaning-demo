package com.luckykuang.lock.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Zookeeper读写锁
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ZkReadWriteLock {

    private final CuratorFramework curatorFramework;

    private static final String LOCK_PATH = "/rwlocks";

    /**
     * 获取读写锁实例
     */
    private InterProcessReadWriteLock getReadWriteLock(String lockKey) {
        String path = LOCK_PATH + "/" + lockKey;
        return new InterProcessReadWriteLock(curatorFramework, path);
    }

    /**
     * 读锁操作
     */
    public void readLock(String lockKey, Runnable runnable) {
        InterProcessReadWriteLock rwLock = getReadWriteLock(lockKey);

        try {
            rwLock.readLock().acquire(10, TimeUnit.SECONDS);
            log.info("获取读锁成功: {}", lockKey);

            try {
                runnable.run();
            } finally {
                rwLock.readLock().release();
                log.info("释放读锁: {}", lockKey);
            }
        } catch (Exception e) {
            log.error("读锁操作失败", e);
            throw new RuntimeException("读锁操作失败", e);
        }
    }

    /**
     * 写锁操作
     */
    public void writeLock(String lockKey, Runnable runnable) {
        InterProcessReadWriteLock rwLock = getReadWriteLock(lockKey);

        try {
            rwLock.writeLock().acquire(10, TimeUnit.SECONDS);
            log.info("获取写锁成功: {}", lockKey);

            try {
                runnable.run();
            } finally {
                rwLock.writeLock().release();
                log.info("释放写锁: {}", lockKey);
            }
        } catch (Exception e) {
            log.error("写锁操作失败", e);
            throw new RuntimeException("写锁操作失败", e);
        }
    }
}
