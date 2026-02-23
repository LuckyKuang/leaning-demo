package com.luckykuang.lock.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Curator分布式锁测试
 */
@Slf4j
@SpringBootTest
class CuratorDistributedLockTest {

    @Autowired
    private CuratorDistributedLock curatorLock;

    @BeforeEach
    void setUp() {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testTryLock() {
        String lockKey = "curator:test:lock:1";
        boolean acquired = curatorLock.tryLock(lockKey);
        assertTrue(acquired, "应该成功获取锁");

        curatorLock.unlock(lockKey);
        log.info("testTryLock passed");
    }

    @Test
    void testLockReentrant() {
        String lockKey = "curator:test:reentrant:1";

        // Curator的InterProcessMutex支持同一线程多次获取锁
        boolean first = curatorLock.tryLock(lockKey);
        assertTrue(first, "第一次获取锁应该成功");

        boolean second = curatorLock.tryLock(lockKey);
        assertTrue(second, "第二次获取锁应该成功（可重入）");

        // 需要释放两次
        curatorLock.unlock(lockKey);
        curatorLock.unlock(lockKey);
        log.info("testLockReentrant passed");
    }

    @Test
    void testExecuteWithLock() {
        String lockKey = "curator:test:execute:1";
        AtomicInteger counter = new AtomicInteger(0);

        boolean result = curatorLock.executeWithLock(lockKey, () -> {
            counter.incrementAndGet();
            log.info("executeWithLock 执行中");
        });

        assertTrue(result, "executeWithLock 应该返回true");
        assertEquals(1, counter.get(), "计数器应该为1");
        log.info("testExecuteWithLock passed");
    }

    @Test
    void testConcurrentLock() throws InterruptedException {
        String lockKey = "curator:test:concurrent:1";
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger counter = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    if (curatorLock.tryLock(lockKey, 60, TimeUnit.SECONDS)) {
                        try {
                            int current = counter.incrementAndGet();
                            log.info("当前值: {}", current);
                            TimeUnit.MILLISECONDS.sleep(50);
                            successCount.incrementAndGet();
                        } finally {
                            curatorLock.unlock(lockKey);
                        }
                    }
                } catch (Exception e) {
                    log.error("并发测试出错", e);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(120, TimeUnit.SECONDS);
        executor.shutdown();
        executor.awaitTermination(60, TimeUnit.SECONDS);

        assertEquals(threadCount, successCount.get(), "所有线程都应该成功获取锁");
        assertEquals(threadCount, counter.get(), "计数器应该等于线程数");
        log.info("testConcurrentLock passed, successCount: {}", successCount.get());
    }

    @Test
    void testLockContention() throws InterruptedException {
        String lockKey = "curator:test:contention:1";
        int threadCount = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);
        List<Long> executionTimes = new java.util.ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    long startTime = System.currentTimeMillis();

                    curatorLock.executeWithLock(lockKey, () -> {
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

        startLatch.countDown();
        endLatch.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        log.info("执行时间列表: {}", executionTimes);
        assertTrue(executionTimes.stream().allMatch(t -> t >= 100), "每个线程至少应该执行100ms");
        log.info("testLockContention passed");
    }
}
