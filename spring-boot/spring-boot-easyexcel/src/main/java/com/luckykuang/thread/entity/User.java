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

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.format.NumberFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.luckykuang.thread.converter.GenderConverter;
import lombok.Data;

import java.time.LocalDate;

/**
 * 用户表
 *
 * ExcelProperty： 核心注解，value属性可用来设置表头名称，converter属性可以用来设置类型转换器；
 * ColumnWidth： 用于设置表格列的宽度；
 * DateTimeFormat： 用于设置日期转换格式；
 * NumberFormat： 用于设置数字转换格式。
 *
 * @author luckykuang
 * @date 2023/7/4 14:49
 */
@Data
@TableName("thread_user")
public class User {

    @TableId(value = "id",type = IdType.AUTO)
    @ExcelProperty("用户编号")
    @ColumnWidth(20)
    private Long id;

    @ExcelProperty("用户名")
    @ColumnWidth(20)
    private String username;

    @ExcelIgnore
    private String password;

    @ExcelProperty("昵称")
    @ColumnWidth(20)
    private String nickname;

    @ExcelProperty("生日")
    @ColumnWidth(20)
    @DateTimeFormat("yyyy-MM-dd")
    private LocalDate birthday;

    @ExcelProperty("手机号")
    @ColumnWidth(20)
    private String phone;

    @ExcelProperty("身高（米）")
    @NumberFormat("#.##")
    @ColumnWidth(20)
    private Double height;

    @ExcelProperty(value = "性别", converter = GenderConverter.class)
    @ColumnWidth(10)
    private Integer gender;
}
