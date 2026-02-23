package com.luckykuang.lock;

import com.luckykuang.lock.service.BasicRedisLock;
import com.luckykuang.lock.service.OrderService;
import com.luckykuang.lock.service.ReentrantRedisLock;
import com.luckykuang.lock.service.SafeRedisLock;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Redis分布式锁单元测试
 *
 * @author LuckyKuang
 * @since 2025-01-15
 */
@Slf4j
@SpringBootTest
class RedisLockTest {

    @Autowired(required = false)
    private StringRedisTemplate redisTemplate;

    @Autowired(required = false)
    private BasicRedisLock basicRedisLock;

    @Autowired(required = false)
    private SafeRedisLock safeRedisLock;

    @Autowired(required = false)
    private ReentrantRedisLock reentrantRedisLock;

    @Autowired(required = false)
    private OrderService orderService;

    private boolean redisAvailable = false;

    @BeforeEach
    void setUp() {
        try {
            // 测试Redis连接
            if (redisTemplate != null) {
                redisTemplate.getConnectionFactory().getConnection().ping();
                redisAvailable = true;
                // 清理Redis测试数据
                redisTemplate.getConnectionFactory().getConnection().flushDb();
                // 重置库存
                if (orderService != null) {
                    orderService.resetStock();
                }
                log.info("Redis连接正常,开始测试");
            }
        } catch (Exception e) {
            redisAvailable = false;
            log.warn("Redis连接失败,跳过需要Redis的测试: {}", e.getMessage());
        }
    }

    @Test
    void testBasicRedisLock_TryLock() {
        Assumptions.assumeTrue(redisAvailable, "Redis不可用,跳过测试");
        // 测试获取锁
        boolean locked = basicRedisLock.tryLock("test:lock");
        assertTrue(locked, "应该成功获取锁");

        // 测试重复获取锁（应该失败）
        boolean lockedAgain = basicRedisLock.tryLock("test:lock");
        assertFalse(lockedAgain, "重复获取锁应该失败");

        // 释放锁
        basicRedisLock.unlock("test:lock");

        // 再次获取锁（应该成功）
        boolean lockedAfterRelease = basicRedisLock.tryLock("test:lock");
        assertTrue(lockedAfterRelease, "释放锁后应该能再次获取");

        // 清理
        basicRedisLock.unlock("test:lock");
    }

    @Test
    void testBasicRedisLock_ExecuteWithLock() {
        Assumptions.assumeTrue(redisAvailable, "Redis不可用,跳过测试");
        final AtomicInteger counter = new AtomicInteger(0);

        // 测试带锁执行
        basicRedisLock.executeWithLock("test:execute", () -> {
            counter.incrementAndGet();
        });

        assertEquals(1, counter.get(), "应该执行一次");
    }

    @Test
    void testSafeRedisLock_PreventMistakenDelete() {
        Assumptions.assumeTrue(redisAvailable, "Redis不可用,跳过测试");
        // 线程A获取锁
        boolean lockedA = safeRedisLock.tryLock("test:safe", 10, TimeUnit.SECONDS);
        assertTrue(lockedA, "线程A应该成功获取锁");

        // 线程B尝试获取锁（应该失败）
        boolean lockedB = safeRedisLock.tryLock("test:safe", 10, TimeUnit.SECONDS);
        assertFalse(lockedB, "线程B应该获取锁失败");

        // 线程A释放锁
        boolean unlockedA = safeRedisLock.unlock("test:safe");
        assertTrue(unlockedA, "线程A应该成功释放锁");

        // 线程B尝试释放锁（应该失败，因为锁已经不存在）
        boolean unlockedB = safeRedisLock.unlock("test:safe");
        assertFalse(unlockedB, "线程B不应该能释放不存在的锁");
    }

    @Test
    void testReentrantRedisLock_Reentrant() {
        Assumptions.assumeTrue(redisAvailable, "Redis不可用,跳过测试");
        String lockKey = "test:reentrant";

        // 第一次获取锁
        boolean locked1 = reentrantRedisLock.tryLock(lockKey, 30, TimeUnit.SECONDS);
        assertTrue(locked1, "第一次获取锁应该成功");

        // 第二次获取锁（可重入）
        boolean locked2 = reentrantRedisLock.tryLock(lockKey, 30, TimeUnit.SECONDS);
        assertTrue(locked2, "第二次获取锁应该成功（可重入）");

        // 第一次释放锁
        boolean unlocked1 = reentrantRedisLock.unlock(lockKey);
        assertTrue(unlocked1, "第一次释放锁应该成功");

        // 第二次释放锁
        boolean unlocked2 = reentrantRedisLock.unlock(lockKey);
        assertTrue(unlocked2, "第二次释放锁应该成功");

        // 再次尝试释放（应该失败，因为已经完全释放）
        boolean unlocked3 = reentrantRedisLock.unlock(lockKey);
        assertFalse(unlocked3, "完全释放后不应该还能释放");
    }

    @Test
    void testReentrantRedisLock_ExecuteWithLock() {
        Assumptions.assumeTrue(redisAvailable, "Redis不可用,跳过测试");
        String lockKey = "test:reentrant:execute";
        final AtomicInteger counter = new AtomicInteger(0);

        reentrantRedisLock.executeWithLock(lockKey, () -> {
            counter.incrementAndGet();
            // 在锁内部再次获取同一个锁（可重入）
            reentrantRedisLock.executeWithLock(lockKey, () -> {
                counter.incrementAndGet();
            });
        });

        assertEquals(2, counter.get(), "应该执行两次（可重入）");
    }

    @Test
    void testOrderService_ConcurrentDeductStock() throws InterruptedException {
        Assumptions.assumeTrue(redisAvailable, "Redis不可用,跳过测试");
        // 重置库存
        orderService.resetStock();
        int initialStock = orderService.getStock();
        assertEquals(100, initialStock, "初始库存应该为100");

        int threadCount = 10;
        int deductCount = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    Integer result = orderService.deductStock("product001", deductCount);
                    if (result != null) {
                        successCount.incrementAndGet();
                    } else {
                        failCount.incrementAndGet();
                        log.info("线程获取锁失败,返回null");
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        int actualStock = orderService.getStock();
        int expectedStock = 100 - (successCount.get() * deductCount);

        log.info("成功扣减次数: {}, 失败次数: {}, 预期库存: {}, 实际库存: {}",
                successCount.get(), failCount.get(), expectedStock, actualStock);

        // 验证库存扣减正确
        assertEquals(expectedStock, actualStock, "并发扣减库存后库存应该正确");
        // 验证至少有一个线程成功
        assertTrue(successCount.get() > 0, "至少应该有一个线程成功获取锁");
    }

    @Test
    void testOrderService_PayOrder() {
        Assumptions.assumeTrue(redisAvailable, "Redis不可用,跳过测试");
        // 测试支付订单
        assertDoesNotThrow(() -> {
            orderService.payOrder("order123", new java.math.BigDecimal("100.00"));
        }, "支付订单不应该抛出异常");
    }

    @Test
    void testOrderService_CancelOrder() {
        Assumptions.assumeTrue(redisAvailable, "Redis不可用,跳过测试");
        // 测试取消订单
        Boolean result = orderService.cancelOrder("order456");
        assertNotNull(result, "取消订单应该返回结果");
    }

    @Test
    void testConcurrency_WithoutLock() throws InterruptedException {
        // 这个测试演示了不加锁的并发问题
        AtomicInteger unsafeCounter = new AtomicInteger(100);
        int threadCount = 10;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    // 模拟读取-修改-写入操作（非原子性）
                    int current = unsafeCounter.get();
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    unsafeCounter.set(current - 1);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        log.info("不加锁的并发测试结果: {}", unsafeCounter.get());
        // 由于并发问题，实际值可能不等于预期值
    }

    @Test
    void testConcurrency_WithLock() throws InterruptedException {
        Assumptions.assumeTrue(redisAvailable, "Redis不可用,跳过测试");
        // 重置库存
        orderService.resetStock();

        int threadCount = 20;
        int deductCount = 3;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            final int threadNum = i;
            executor.submit(() -> {
                try {
                    Integer result = orderService.deductStock("product002", deductCount);
                    if (result != null) {
                        successCount.incrementAndGet();
                        log.info("线程 {} 扣减库存成功", threadNum);
                    } else {
                        log.info("线程 {} 获取锁失败", threadNum);
                    }
                } catch (Exception e) {
                    log.error("线程 {} 扣减库存失败: {}", threadNum, e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(60, TimeUnit.SECONDS);
        executor.shutdown();

        int expectedStock = 100 - (successCount.get() * deductCount);
        int actualStock = orderService.getStock();

        log.info("加锁的并发测试 - 成功次数: {}, 预期库存: {}, 实际库存: {}",
                successCount.get(), expectedStock, actualStock);

        // 验证库存扣减正确
        assertEquals(expectedStock, actualStock, "使用分布式锁后，并发扣减库存应该正确");
        // 验证至少有一个线程成功
        assertTrue(successCount.get() > 0, "至少应该有一个线程成功获取锁");
    }

    @Test
    void testLockExpireTime() {
        Assumptions.assumeTrue(redisAvailable, "Redis不可用,跳过测试");
        String lockKey = "test:expire";

        // 获取锁，过期时间为2秒
        boolean locked = reentrantRedisLock.tryLock(lockKey, 2, TimeUnit.SECONDS);
        assertTrue(locked, "应该成功获取锁");

        // 等待3秒（超过过期时间）
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 锁应该已过期，可以再次获取
        boolean lockedAgain = reentrantRedisLock.tryLock(lockKey, 2, TimeUnit.SECONDS);
        assertTrue(lockedAgain, "锁过期后应该能再次获取");

        // 清理
        reentrantRedisLock.unlock(lockKey);
    }
}
