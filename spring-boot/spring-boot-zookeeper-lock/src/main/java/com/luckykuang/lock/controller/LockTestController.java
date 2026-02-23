package com.luckykuang.lock.controller;

import com.luckykuang.lock.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * 注解式锁测试控制器
 */
@Tag(name = "锁测试接口", description = "注解式锁测试接口")
@RestController
@RequestMapping("/api/lock")
@RequiredArgsConstructor
public class LockTestController {

    private final OrderService orderService;

    /**
     * 创建订单 - 注解式锁
     */
    @Operation(summary = "创建订单")
    @PostMapping("/order/create")
    public String createOrder() {
        orderService.createOrder();
        return "订单创建成功";
    }

    /**
     * 支付订单 - 注解式锁（SpEL表达式）
     */
    @Operation(summary = "支付订单")
    @PostMapping("/order/pay")
    public String payOrder(@RequestParam String orderId, @RequestParam BigDecimal amount) {
        orderService.payOrder(orderId, amount);
        return "支付成功";
    }

    /**
     * 取消订单 - 快速失败策略
     */
    @Operation(summary = "取消订单")
    @PostMapping("/order/cancel")
    public Boolean cancelOrder(@RequestParam String orderId) {
        return orderService.cancelOrder(orderId);
    }

    /**
     * 扣减库存 - 注解式锁
     */
    @Operation(summary = "扣减库存")
    @PostMapping("/stock/deduct")
    public String deductStock(@RequestParam String productId, @RequestParam int count) {
        orderService.deductStock(productId, count);
        return "库存扣减成功";
    }
}
