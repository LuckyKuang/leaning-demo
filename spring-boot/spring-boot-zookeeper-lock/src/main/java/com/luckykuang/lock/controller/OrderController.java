package com.luckykuang.lock.controller;

import com.luckykuang.lock.service.ZkDistributedLock;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试控制器 - 手动实现锁
 */
@Tag(name = "订单接口", description = "订单相关接口")
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final ZkDistributedLock zkLock;

    /**
     * 创建订单接口
     */
    @Operation(summary = "创建订单")
    @PostMapping("/create")
    public String createOrder(@RequestParam String orderId) {
        String lockKey = "order:create:" + orderId;

        zkLock.executeWithLock(lockKey, () -> {
            System.out.println("创建订单: " + orderId);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("订单创建完成: " + orderId);
        });

        return "订单创建成功";
    }
}
