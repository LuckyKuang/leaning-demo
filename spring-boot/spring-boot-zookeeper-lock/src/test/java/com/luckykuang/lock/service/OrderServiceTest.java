package com.luckykuang.lock.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * 订单服务测试 - 注解式锁
 */
@Slf4j
@SpringBootTest
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testCreateOrder() {
        orderService.createOrder();
        log.info("testCreateOrder passed");
    }

    @Test
    void testPayOrder() {
        String orderId = "ORD001";
        BigDecimal amount = new BigDecimal("99.99");
        orderService.payOrder(orderId, amount);
        log.info("testPayOrder passed");
    }

    @Test
    void testCancelOrder() {
        String orderId = "ORD002";
        Boolean result = orderService.cancelOrder(orderId);
        assertNotNull(result, "取消订单结果不应为null");
        log.info("testCancelOrder passed, result: {}", result);
    }

    @Test
    void testDeductStock() {
        String productId = "PROD001";
        int count = 10;
        orderService.deductStock(productId, count);
        log.info("testDeductStock passed");
    }

    @Test
    void testConcurrentCreateOrder() throws InterruptedException {
        int threadCount = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    orderService.createOrder();
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    log.error("创建订单出错", e);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(120, TimeUnit.SECONDS);
        executor.shutdown();
        executor.awaitTermination(60, TimeUnit.SECONDS);

        assertEquals(threadCount, successCount.get(), "所有订单都应该创建成功");
        log.info("testConcurrentCreateOrder passed, successCount: {}", successCount.get());
    }

    @Test
    void testConcurrentPayOrder() throws InterruptedException {
        String orderId = "ORD003";
        int threadCount = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            executor.submit(() -> {
                try {
                    orderService.payOrder(orderId, new BigDecimal("10" + index));
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    log.error("支付订单出错", e);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(120, TimeUnit.SECONDS);
        executor.shutdown();
        executor.awaitTermination(60, TimeUnit.SECONDS);

        assertEquals(threadCount, successCount.get(), "所有支付都应该成功");
        log.info("testConcurrentPayOrder passed, successCount: {}", successCount.get());
    }

    @Test
    void testConcurrentDeductStock() throws InterruptedException {
        String productId = "PROD002";
        int threadCount = 3;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    orderService.deductStock(productId, 5);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    log.error("扣减库存出错", e);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(120, TimeUnit.SECONDS);
        executor.shutdown();
        executor.awaitTermination(60, TimeUnit.SECONDS);

        assertEquals(threadCount, successCount.get(), "所有库存扣减都应该成功");
        log.info("testConcurrentDeductStock passed, successCount: {}", successCount.get());
    }

    @Test
    void testDifferentOrderPay() throws InterruptedException {
        int threadCount = 3;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            final String orderId = "ORD" + (100 + i);
            executor.submit(() -> {
                try {
                    orderService.payOrder(orderId, new BigDecimal("100"));
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    log.error("支付订单出错", e);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(120, TimeUnit.SECONDS);
        executor.shutdown();
        executor.awaitTermination(60, TimeUnit.SECONDS);

        assertEquals(threadCount, successCount.get(), "所有不同订单的支付都应该成功");
        log.info("testDifferentOrderPay passed, successCount: {}", successCount.get());
    }
}
