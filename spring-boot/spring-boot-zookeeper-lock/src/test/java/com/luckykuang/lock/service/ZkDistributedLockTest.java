package com.luckykuang.lock.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Zookeeper分布式锁测试 - 手动实现
 */
@Slf4j
@SpringBootTest
class ZkDistributedLockTest {

    @Autowired
    private ZkDistributedLock zkLock;

    @BeforeEach
    void setUp() {
        // 等待Zookeeper连接建立
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testTryLock() {
        String lockKey = "test:lock:1";
        boolean acquired = zkLock.tryLock(lockKey);
        assertTrue(acquired, "应该成功获取锁");

        zkLock.unlock(lockKey);
        log.info("testTryLock passed");
    }

    @Test
    void testTryLockWithTimeout() {
        String lockKey = "test:lock:timeout:1";
        boolean acquired = zkLock.tryLock(lockKey, 5, TimeUnit.SECONDS);
        assertTrue(acquired, "应该成功获取锁");

        zkLock.unlock(lockKey);
        log.info("testTryLockWithTimeout passed");
    }

    @Test
    void testLockReentrant() {
        String lockKey = "test:lock:reentrant:1";
        boolean first = zkLock.tryLock(lockKey);
        assertTrue(first, "第一次获取锁应该成功");

        // 注意：手动实现的锁不支持同一线程重入，需要释放后再获取
        zkLock.unlock(lockKey);

        boolean second = zkLock.tryLock(lockKey);
        assertTrue(second, "第二次获取锁应该成功");

        zkLock.unlock(lockKey);
        log.info("testLockReentrant passed");
    }

    @Test
    void testExecuteWithLock() {
        String lockKey = "test:lock:execute:1";
        AtomicInteger counter = new AtomicInteger(0);

        zkLock.executeWithLock(lockKey, () -> {
            counter.incrementAndGet();
            log.info("executeWithLock 执行中");
        });

        assertEquals(1, counter.get(), "计数器应该为1");
        log.info("testExecuteWithLock passed");
    }

    @Test
    void testConcurrentLock() throws InterruptedException {
        String lockKey = "test:lock:concurrent:1";
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger counter = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    if (zkLock.tryLock(lockKey, 10, TimeUnit.SECONDS)) {
                        try {
                            int current = counter.incrementAndGet();
                            log.info("当前值: {}", current);
                            TimeUnit.MILLISECONDS.sleep(50);
                            successCount.incrementAndGet();
                        } finally {
                            zkLock.unlock(lockKey);
                        }
                    }
                } catch (Exception e) {
                    log.error("并发测试出错", e);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        assertEquals(threadCount, successCount.get(), "所有线程都应该成功获取锁");
        assertEquals(threadCount, counter.get(), "计数器应该等于线程数");
        log.info("testConcurrentLock passed, successCount: {}", successCount.get());
    }

    @Test
    void testLockContention() throws InterruptedException {
        String lockKey = "test:lock:contention:1";
        int threadCount = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);
        List<Long> executionTimes = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await(); // 等待所有线程准备就绪
                    long startTime = System.currentTimeMillis();

                    zkLock.executeWithLock(lockKey, () -> {
                        try {
                            TimeUnit.MILLISECONDS.sleep(100);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    });

                    long endTime = System.currentTimeMillis();
                    synchronized (executionTimes) {
                        executionTimes.add(endTime - startTime);
                    }
                } catch (Exception e) {
                    log.error("锁竞争测试出错", e);
                } finally {
                    endLatch.countDown();
                }
            });
        }

        startLatch.countDown(); // 同时释放所有线程
        endLatch.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        log.info("执行时间列表: {}", executionTimes);
        assertTrue(executionTimes.stream().allMatch(t -> t >= 100), "每个线程至少应该执行100ms（持有锁的时间）");
        log.info("testLockContention passed");
    }

    @Test
    void testDifferentKeys() {
        String lockKey1 = "test:lock:different:1";
        String lockKey2 = "test:lock:different:2";

        boolean acquired1 = zkLock.tryLock(lockKey1);
        boolean acquired2 = zkLock.tryLock(lockKey2);

        assertTrue(acquired1, "应该成功获取锁1");
        assertTrue(acquired2, "应该成功获取锁2");

        zkLock.unlock(lockKey1);
        zkLock.unlock(lockKey2);
        log.info("testDifferentKeys passed");
    }

    @Test
    void testLockTimeout() {
        String lockKey = "test:lock:timeout:long";

        // 第一个线程获取锁并长时间持有
        new Thread(() -> {
            zkLock.executeWithLock(lockKey, () -> {
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }).start();

        // 等待第一个线程获取锁
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 第二个线程尝试获取锁，设置较短超时
        boolean acquired = zkLock.tryLock(lockKey, 1, TimeUnit.SECONDS);
        assertFalse(acquired, "应该获取锁超时");

        log.info("testLockTimeout passed");
    }
}
