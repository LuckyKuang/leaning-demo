package com.luckykuang.lock.controller;

import com.luckykuang.lock.service.BasicRedisLock;
import com.luckykuang.lock.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 测试控制器
 *
 * @author LuckyKuang
 * @since 2025-01-15
 */
@Slf4j
@RestController
@RequestMapping("/api/lock")
@RequiredArgsConstructor
public class LockController {

    private final BasicRedisLock basicRedisLock;
    private final OrderService orderService;

    /**
     * 库存扣减接口
     */
    @PostMapping("/deduct")
    public String deductStock(@RequestParam String productId, @RequestParam Integer count) {
        String lockKey = "stock:" + productId;

        // 使用分布式锁
        basicRedisLock.executeWithLock(lockKey, () -> {
            // 模拟业务处理
            System.out.println("扣减库存 " + productId + " 数量: " + count);

            try {
                // 模拟耗时操作
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("库存扣减完成");
        });

        return "库存扣减成功";
    }

    /**
     * 创建订单
     */
    @PostMapping("/order/create")
    public String createOrder() {
        orderService.createOrder();
        return "订单创建成功";
    }

    /**
     * 支付订单
     */
    @PostMapping("/order/pay")
    public String payOrder(@RequestParam String orderId, @RequestParam BigDecimal amount) {
        orderService.payOrder(orderId, amount);
        return "订单支付成功";
    }

    /**
     * 取消订单
     */
    @PostMapping("/order/cancel")
    public Boolean cancelOrder(@RequestParam String orderId) {
        return orderService.cancelOrder(orderId);
    }

    /**
     * 扣减库存（使用注解）
     */
    @PostMapping("/stock/deduct")
    public Integer deductStockWithAnnotation(@RequestParam String productId, @RequestParam Integer count) {
        return orderService.deductStock(productId, count);
    }

    /**
     * 获取库存
     */
    @GetMapping("/stock")
    public Integer getStock() {
        return orderService.getStock();
    }

    /**
     * 重置库存
     */
    @PostMapping("/stock/reset")
    public String resetStock() {
        orderService.resetStock();
        return "库存重置成功";
    }
}
