/*
 * Copyright 2015-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.luckykuang.thread.controller;

import com.luckykuang.thread.entity.Order;
import com.luckykuang.thread.entity.User;
import com.luckykuang.thread.service.OrderService;
import com.luckykuang.thread.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author luckykuang
 * @date 2023/7/4 15:10
 */
@RestController
@RequestMapping("/excel")
@RequiredArgsConstructor
public class ExcelController {

    private final UserService userService;
    private final OrderService orderService;

    /**
     * 用户信息导出
     * @param response 响应对象
     */
    @GetMapping("/export/user")
    public void exportUserExcel(HttpServletResponse response) {
        userService.exportUserExcel(response);
    }

    /**
     * 用户信息导入
     * 注意：导入信息没有做任何校验，此处只是一个参考案例
     * @param file 导入文件 - 此处没有对导入文件做校验
     * @return 导入的用户信息
     */
    @PostMapping("/import/user")
    public ResponseEntity<List<User>> importUserExcel(@RequestPart(value = "file") MultipartFile file) {
        return userService.importUserExcel(file);
    }

    /**
     * 订单信息导出(复杂导出)
     * @param response 响应对象
     */
    @GetMapping("/export/order")
    public void exportOrderExcel(HttpServletResponse response) {
        orderService.exportOrderExcel(response);
    }

    /**
     * 订单信息导入(线程池导入)
     * 注意：导入信息没有做任何校验，此处只是一个参考案例
     * @param file 导入文件 - 此处没有对导入文件做校验
     * @return 导入的订单信息
     */
    @PostMapping("/import/order")
    public ResponseEntity<List<Order>> importOrderExcel(@RequestPart(value = "file") MultipartFile file) {
        return orderService.importOrderExcel(file);
    }
}
