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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Zookeeper读写锁测试
 */
@Slf4j
@SpringBootTest
class ZkReadWriteLockTest {

    @Autowired
    private ZkReadWriteLock readWriteLock;

    @BeforeEach
    void setUp() {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testReadLock() {
        String lockKey = "rwlock:test:read:1";
        AtomicInteger counter = new AtomicInteger(0);

        readWriteLock.readLock(lockKey, () -> {
            counter.incrementAndGet();
            log.info("读锁执行中");
        });

        assertEquals(1, counter.get(), "读锁应该执行成功");
        log.info("testReadLock passed");
    }

    @Test
    void testWriteLock() {
        String lockKey = "rwlock:test:write:1";
        AtomicInteger counter = new AtomicInteger(0);

        readWriteLock.writeLock(lockKey, () -> {
            counter.incrementAndGet();
            log.info("写锁执行中");
        });

        assertEquals(1, counter.get(), "写锁应该执行成功");
        log.info("testWriteLock passed");
    }

    @Test
    void testConcurrentReadLock() throws InterruptedException {
        String lockKey = "rwlock:test:concurrent:read:1";
        int threadCount = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);
        AtomicInteger counter = new AtomicInteger(0);
        List<Long> executionTimes = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    long startTime = System.currentTimeMillis();

                    readWriteLock.readLock(lockKey, () -> {
                        try {
                            counter.incrementAndGet();
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
                    log.error("并发读测试出错", e);
                } finally {
                    endLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        endLatch.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        assertEquals(threadCount, counter.get(), "所有读操作都应该成功");
        log.info("并发读执行时间列表: {}", executionTimes);

        // 读锁是共享的，所以多个线程可以同时获取读锁
        log.info("testConcurrentReadLock passed");
    }

    @Test
    void testConcurrentWriteLock() throws InterruptedException {
        String lockKey = "rwlock:test:concurrent:write:1";
        int threadCount = 3;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);
        AtomicInteger counter = new AtomicInteger(0);
        List<Long> executionTimes = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    long startTime = System.currentTimeMillis();

                    readWriteLock.writeLock(lockKey, () -> {
                        try {
                            counter.incrementAndGet();
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
                    log.error("并发写测试出错", e);
                } finally {
                    endLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        endLatch.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        assertEquals(threadCount, counter.get(), "所有写操作都应该成功");
        log.info("并发写执行时间列表: {}", executionTimes);

        // 写锁是独占的，每个线程至少要等待100ms持有锁的时间
        assertTrue(executionTimes.stream().allMatch(t -> t >= 100), "每个线程至少应该执行100ms");
        log.info("testConcurrentWriteLock passed");
    }

    @Test
    void testReadWriteIsolation() throws InterruptedException {
        String lockKey = "rwlock:test:isolation:1";
        AtomicInteger readCounter = new AtomicInteger(0);
        AtomicInteger writeCounter = new AtomicInteger(0);

        // 一个写操作
        ExecutorService writeExecutor = Executors.newSingleThreadExecutor();
        CountDownLatch writeLatch = new CountDownLatch(1);

        writeExecutor.submit(() -> {
            readWriteLock.writeLock(lockKey, () -> {
                try {
                    writeCounter.incrementAndGet();
                    log.info("写操作开始，持有锁500ms");
                    TimeUnit.MILLISECONDS.sleep(500);
                    log.info("写操作结束");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            writeLatch.countDown();
        });

        // 等待写操作开始
        TimeUnit.MILLISECONDS.sleep(100);

        // 多个读操作
        ExecutorService readExecutor = Executors.newFixedThreadPool(3);
        CountDownLatch readLatch = new CountDownLatch(3);
        List<Long> readTimes = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            readExecutor.submit(() -> {
                try {
                    long startTime = System.currentTimeMillis();

                    readWriteLock.readLock(lockKey, () -> {
                        try {
                            readCounter.incrementAndGet();
                            TimeUnit.MILLISECONDS.sleep(50);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    });

                    long endTime = System.currentTimeMillis();
                    synchronized (readTimes) {
                        readTimes.add(endTime - startTime);
                    }
                } catch (Exception e) {
                    log.error("读操作出错", e);
                } finally {
                    readLatch.countDown();
                }
            });
        }

        writeLatch.await(10, TimeUnit.SECONDS);
        readLatch.await(10, TimeUnit.SECONDS);
        writeExecutor.shutdown();
        readExecutor.shutdown();

        assertEquals(1, writeCounter.get(), "写操作应该执行1次");
        assertEquals(3, readCounter.get(), "读操作应该执行3次");
        log.info("读操作执行时间列表: {}", readTimes);

        // 读操作应该等待写操作完成
        assertTrue(readTimes.stream().allMatch(t -> t >= 500), "读操作应该等待写操作完成（至少500ms）");
        log.info("testReadWriteIsolation passed");
    }
}
