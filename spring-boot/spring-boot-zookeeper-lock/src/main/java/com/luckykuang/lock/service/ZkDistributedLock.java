package com.luckykuang.lock.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Zookeeper分布式锁 - 手动实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ZkDistributedLock {

    private final CuratorFramework curatorFramework;

    private static final String LOCK_PATH = "/locks";
    private static final long DEFAULT_TIMEOUT = 30;

    /** 当前线程创建的节点路径 */
    private final ThreadLocal<String> currentNode = new ThreadLocal<>();

    /** 同步原语 */
    private final ThreadLocal<CountDownLatch> latch = new ThreadLocal<>();

    /**
     * 尝试获取锁
     */
    public boolean tryLock(String lockKey) {
        return tryLock(lockKey, DEFAULT_TIMEOUT, TimeUnit.SECONDS);
    }

    /**
     * 尝试获取锁（带超时时间）
     */
    public boolean tryLock(String lockKey, long timeout, TimeUnit unit) {
        String lockPath = LOCK_PATH + "/" + lockKey;

        try {
            // 1. 创建锁的父节点（如果不存在）
            if (curatorFramework.checkExists().forPath(lockPath) == null) {
                try {
                    curatorFramework.create()
                            .creatingParentsIfNeeded()
                            .forPath(lockPath);
                } catch (Exception e) {
                    log.debug("锁节点已存在: {}", lockPath);
                }
            }

            // 2. 创建临时顺序节点
            String nodeName = curatorFramework.create()
                    .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                    .forPath(lockPath + "/node-");

            currentNode.set(nodeName);
            log.info("创建节点成功: {}", nodeName);

            // 3. 获取所有子节点并排序
            List<String> children = curatorFramework.getChildren().forPath(lockPath);
            Collections.sort(children);

            // 4. 获取当前节点的序号
            String currentNodeName = nodeName.substring(nodeName.lastIndexOf('/') + 1);
            int currentIndex = children.indexOf(currentNodeName);

            // 5. 判断是否为最小的节点
            if (currentIndex == 0) {
                log.info("获取锁成功: {}", currentNodeName);
                return true;
            }

            // 6. 不是最小节点，监听前一个节点
            String previousNodeName = children.get(currentIndex - 1);
            String previousNodePath = lockPath + "/" + previousNodeName;

            log.info("等待前一个节点释放: {}", previousNodePath);

            // 创建CountDownLatch用于阻塞
            CountDownLatch countDownLatch = new CountDownLatch(1);
            latch.set(countDownLatch);

            // 监听前一个节点的删除事件
            Watcher watcher = new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if (event.getType() == Event.EventType.NodeDeleted) {
                        log.info("前一个节点已删除: {}", previousNodePath);
                        countDownLatch.countDown();
                    }
                }
            };

            // 检查前一个节点是否存在（防止竞态条件）
            if (curatorFramework.checkExists().usingWatcher(watcher).forPath(previousNodePath) != null) {
                boolean await = countDownLatch.await(timeout, unit);
                if (!await) {
                    log.warn("获取锁超时: {}", currentNodeName);
                    unlock(lockKey);
                    return false;
                }
            }

            log.info("获取锁成功: {}", currentNodeName);
            return true;

        } catch (Exception e) {
            log.error("获取锁失败", e);
            return false;
        }
    }

    /**
     * 释放锁
     */
    public void unlock(String lockKey) {
        String nodeName = currentNode.get();
        if (nodeName == null) {
            log.warn("没有可释放的锁");
            return;
        }

        try {
            curatorFramework.delete().forPath(nodeName);
            log.info("释放锁成功: {}", nodeName);
        } catch (Exception e) {
            log.error("释放锁失败", e);
        } finally {
            currentNode.remove();
            CountDownLatch countDownLatch = latch.get();
            if (countDownLatch != null) {
                countDownLatch.countDown();
                latch.remove();
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
