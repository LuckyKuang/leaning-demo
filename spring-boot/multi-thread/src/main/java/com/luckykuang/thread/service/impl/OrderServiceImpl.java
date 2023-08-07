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

package com.luckykuang.thread.service.impl;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luckykuang.thread.entity.Order;
import com.luckykuang.thread.mapper.OrderMapper;
import com.luckykuang.thread.service.OrderService;
import com.luckykuang.thread.strategy.ExcelMergeStrategy;
import com.luckykuang.thread.utils.ResponseUtils;
import com.luckykuang.thread.utils.ThreadPoolUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;

/**
 * @author luckykuang
 * @date 2023/7/5 15:05
 */
@Slf4j
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper,Order> implements OrderService {

    @Override
    public void exportOrderExcel(HttpServletResponse response) {
        try {
            ResponseUtils.setExcelResponseProp(response, "订单列表");
            List<Order> orderList = list();
            EasyExcelFactory.write(response.getOutputStream())
                    .head(Order.class)
                    .registerWriteHandler(new ExcelMergeStrategy(Order.class))
                    .excelType(ExcelTypeEnum.XLSX)
                    .sheet("订单列表")
                    .doWrite(orderList);
        } catch (IOException e) {
            log.error("订单列表导出异常: ",e);
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ResponseEntity<List<Order>> importOrderExcel(MultipartFile file) {

        List<Order> orderList;
        try {
            // 读取excel文件数据
            orderList = EasyExcelFactory.read(file.getInputStream())
                    .head(Order.class)
                    .sheet()
                    .doReadSync();
        } catch (IOException e) {
            log.error("订单列表导入文件读取异常: ",e);
            throw new RuntimeException(e);
        }

        // 获取线程池
        ExecutorService executorService = ThreadPoolUtils.getThreadPoolExecutor();

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        // 每次导入的数据量
        int batchSize = 1000;

        for (int i = 0; i < orderList.size(); i += batchSize) {
            // 每次拆分后的结束索引
            int endIndex = Math.min(i + batchSize, orderList.size());
            // 每次拆分后的数据
            List<Order> subList = orderList.subList(i, endIndex);
            // 异步处理数据导入 runAsync()是没有回调的异步，supplyAsync()是有回调的异步
            futures.add(CompletableFuture.runAsync(() -> saveBatch(subList), executorService));
        }

        try {
            // 等待所有异步任务完成(此处会阻塞，直到所有线程处理完成)
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        } catch (CompletionException e) {
            log.warn("线程执行异常：",e);
            throw new RuntimeException(e);
        } catch (CancellationException e) {
            log.error("线程已被取消：",e);
            throw new RuntimeException(e);
        } finally {
            // 关闭线程池
            executorService.shutdown();
        }

        return ResponseEntity.ok(orderList);
    }
}
