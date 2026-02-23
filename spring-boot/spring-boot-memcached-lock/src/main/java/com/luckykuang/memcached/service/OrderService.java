package com.luckykuang.memcached.service;

import com.luckykuang.memcached.annotation.McLock;
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
    @McLock(key = "order:create", expireTime = 10, timeUnit = TimeUnit.SECONDS)
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
    @McLock(
            key = "order:pay:#{#orderId}",
            expireTime = 30,
            timeUnit = TimeUnit.SECONDS,
            failStrategy = McLock.LockFailStrategy.THROW_EXCEPTION
    )
    public void payOrder(String orderId, BigDecimal amount) {
        log.info("支付订单: {}, 金额: {}", orderId, amount);
    }

    /**
     * 库存扣减 - 支持自动续期
     */
    @McLock(
            key = "stock:#{#productId}",
            expireTime = 30,
            timeUnit = TimeUnit.SECONDS,
            autoRenewal = true
    )
    public void deductStock(String productId, int count) {
        log.info("扣减库存: {}, 数量: {}", productId, count);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        log.info("库存扣减完成: {}", productId);
    }

    /**
     * 取消订单 - 快速失败策略
     */
    @McLock(
            key = "order:cancel:#{#orderId}",
            expireTime = 5,
            failStrategy = McLock.LockFailStrategy.FAIL_FAST
    )
    public boolean cancelOrder(String orderId) {
        log.info("取消订单: {}", orderId);
        return true;
    }
}
