package com.luckykuang.lock.controller;

import com.luckykuang.lock.service.ZkReadWriteLock;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 读写锁测试控制器
 */
@Tag(name = "商品接口", description = "商品相关接口")
@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ZkReadWriteLock readWriteLock;

    /**
     * 查询商品 - 使用读锁（共享）
     */
    @Operation(summary = "查询商品")
    @GetMapping("/get/{productId}")
    public String getProduct(@PathVariable String productId) {
        readWriteLock.readLock("product:" + productId, () -> {
            System.out.println("查询商品: " + productId);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        return "商品信息";
    }

    /**
     * 更新商品 - 使用写锁（独占）
     */
    @Operation(summary = "更新商品")
    @PostMapping("/update/{productId}")
    public String updateProduct(@PathVariable String productId) {
        readWriteLock.writeLock("product:" + productId, () -> {
            System.out.println("更新商品: " + productId);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        return "更新成功";
    }
}
