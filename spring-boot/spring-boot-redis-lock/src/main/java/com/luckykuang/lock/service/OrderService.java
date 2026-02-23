package com.luckykuang.lock.service;

import com.luckykuang.lock.annotation.RedisLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 订单服务
 *
 * @author LuckyKuang
 * @since 2025-01-15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final AtomicInteger stock = new AtomicInteger(100);

    /**
     * 创建订单 - 使用固定key
     */
    @RedisLock(key = "order:create", expireTime = 10, timeUnit = TimeUnit.SECONDS)
    public void createOrder() {
        log.info("创建订单...");
        // 业务逻辑
    }

    /**
     * 支付订单 - 使用SpEL表达式
     */
    @RedisLock(
            key = "'order:pay:' + #orderId",
            expireTime = 30,
            timeUnit = TimeUnit.SECONDS,
            failStrategy = RedisLock.LockFailStrategy.THROW_EXCEPTION
    )
    public void payOrder(String orderId, BigDecimal amount) {
        log.info("支付订单: {}, 金额: {}", orderId, amount);
        // 业务逻辑
    }

    /**
     * 取消订单 - 快速失败策略
     */
    @RedisLock(
            key = "'order:cancel:' + #orderId",
            expireTime = 5,
            failStrategy = RedisLock.LockFailStrategy.FAIL_FAST
    )
    public boolean cancelOrder(String orderId) {
        log.info("取消订单: {}", orderId);
        // 业务逻辑
        return true;
    }

    /**
     * 库存扣减
     */
    @RedisLock(
            key = "'stock:' + #productId",
            expireTime = 30,
            timeUnit = TimeUnit.SECONDS,
            failStrategy = RedisLock.LockFailStrategy.FAIL_FAST
    )
    public int deductStock(String productId, int count) {
        String threadInfo = Thread.currentThread().getName() + ":" + Thread.currentThread().getId();
        log.info("线程 {} 开始扣减库存: {}, 数量: {}", threadInfo, productId, count);

        // 模拟耗时业务操作
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int currentStock = stock.getAndAdd(-count);
        log.info("线程 {} 库存扣减完成: {}, 扣减前库存: {}, 扣减后库存: {}",
                threadInfo, productId, currentStock, currentStock - count);

        return currentStock - count;
    }

    /**
     * 获取当前库存
     */
    public int getStock() {
        return stock.get();
    }

    /**
     * 重置库存
     */
    public void resetStock() {
        stock.set(100);
    }
}
