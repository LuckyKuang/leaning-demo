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
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * @author luckykuang
 * @date 2023/7/5 15:05
 */
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
    public ResponseEntity<List<Order>> importOrderExcel(MultipartFile file) {
        try {
            List<Order> orderList = EasyExcelFactory.read(file.getInputStream())
                    .head(Order.class)
                    .sheet()
                    .doReadSync();
            return ResponseEntity.ok(orderList);
        } catch (IOException e) {
            log.error("订单列表导入异常: ",e);
            throw new IllegalArgumentException(e);
        }
    }
}
