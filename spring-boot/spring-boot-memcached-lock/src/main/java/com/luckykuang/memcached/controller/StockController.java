package com.luckykuang.memcached.controller;

import com.luckykuang.memcached.service.BasicMemcachedLock;
import com.luckykuang.memcached.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * 测试控制器
 */
@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
public class StockController {

    private final BasicMemcachedLock memcachedLock;
    private final OrderService orderService;

    /**
     * 库存扣减接口
     */
    @PostMapping("/deduct")
    public String deductStock(@RequestParam String productId, @RequestParam Integer count) {
        String lockKey = "stock:" + productId;

        memcachedLock.executeWithLock(lockKey, () -> {
            System.out.println("扣减库存 " + productId + " 数量: " + count);

            try {
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
     * 扣减库存（注解方式）
     */
    @PostMapping("/stock/deduct")
    public String deductStockAnnotation(@RequestParam String productId, @RequestParam Integer count) {
        orderService.deductStock(productId, count);
        return "库存扣减成功";
    }

    /**
     * 取消订单
     */
    @PostMapping("/order/cancel")
    public Boolean cancelOrder(@RequestParam String orderId) {
        return orderService.cancelOrder(orderId);
    }
}
