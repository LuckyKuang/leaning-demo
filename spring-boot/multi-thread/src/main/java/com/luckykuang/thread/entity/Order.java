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

package com.luckykuang.thread.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.luckykuang.thread.aspect.ExcelMerge;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单表
 * @author luckykuang
 * @date 2023/7/5 11:19
 */
@Data
@TableName("thread_order")
public class Order {

    @TableId(value = "id",type = IdType.INPUT)
    @ExcelProperty(value = "订单主键")
    @ColumnWidth(16)
    @ExcelMerge(merge = true, isPrimaryKey = true)
    private String id;

    @ExcelProperty(value = "订单编号")
    @ColumnWidth(20)
    @ExcelMerge(merge = true)
    private String orderId;

    @ExcelProperty(value = "收货地址")
    @ExcelMerge(merge = true)
    @ColumnWidth(20)
    private String address;

    @ExcelProperty(value = "创建时间")
    @ColumnWidth(20)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelMerge(merge = true)
    private LocalDateTime createTime;

    @ExcelProperty(value = {"商品信息", "商品编号"})
    @ColumnWidth(20)
    private String productId;

    @ExcelProperty(value = {"商品信息", "商品名称"})
    @ColumnWidth(20)
    private String name;

    @ExcelProperty(value = {"商品信息", "商品标题"})
    @ColumnWidth(30)
    private String subtitle;

    @ExcelProperty(value = {"商品信息", "品牌名称"})
    @ColumnWidth(20)
    private String brandName;

    @ExcelProperty(value = {"商品信息", "商品价格"})
    @ColumnWidth(20)
    private BigDecimal price;

    @ExcelProperty(value = {"商品信息", "商品数量"})
    @ColumnWidth(20)
    private Integer count;
}
