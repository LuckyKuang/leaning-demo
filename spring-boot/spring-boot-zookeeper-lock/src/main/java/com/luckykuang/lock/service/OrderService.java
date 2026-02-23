package com.luckykuang.lock.service;

import com.luckykuang.lock.annotation.ZkLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

/**
 * 订单服务
 */
@Slf4j
@Service
public class OrderService {

    /**
     * 创建订单 - 使用固定key
     */
    @ZkLock(key = "order:create", timeout = 60, timeUnit = TimeUnit.SECONDS)
    public void createOrder() {
        log.info("创建订单...");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 支付订单 - 使用SpEL表达式
     */
    @ZkLock(
            key = "order:pay:#{#orderId}",
            timeout = 60,
            timeUnit = TimeUnit.SECONDS,
            failStrategy = ZkLock.LockFailStrategy.THROW_EXCEPTION
    )
    public void payOrder(String orderId, BigDecimal amount) {
        log.info("支付订单: {}, 金额: {}", orderId, amount);
    }

    /**
     * 取消订单 - 快速失败策略
     */
    @ZkLock(
            key = "order:cancel:#{#orderId}",
            timeout = 60,
            failStrategy = ZkLock.LockFailStrategy.FAIL_FAST
    )
    public boolean cancelOrder(String orderId) {
        log.info("取消订单: {}", orderId);
        return true;
    }

    /**
     * 库存扣减 - 使用SpEL表达式
     */
    @ZkLock(key = "stock:#{#productId}", timeout = 60, timeUnit = TimeUnit.SECONDS)
    public void deductStock(String productId, int count) {
        log.info("扣减库存: {}, 数量: {}", productId, count);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("库存扣减完成: {}", productId);
    }
}
