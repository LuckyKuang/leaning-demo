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

package com.luckykuang.statemachine.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author luckykuang
 * @date 2023/10/9 16:36
 */
@Data
@Schema(description = "订单表")
@TableName("tb_order")
public class Order {
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;
    @Schema(description = "订单编码", type = "String", requiredMode = Schema.RequiredMode.REQUIRED)
    private String orderCode;
    @Schema(description = "订单状态", type = "Integer", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer status;
    @Schema(description = "订单名称", type = "String", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
    @Schema(description = "订单价格", type = "BigDecimal", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal price;
    @Schema(description = "删除标记，0未删除  1已删除", type = "Integer", requiredMode = Schema.RequiredMode.REQUIRED, defaultValue = "0")
    private Integer deleteFlag;
    @Schema(description = "创建时间", type = "LocalDateTime")
    private LocalDateTime createTime;
    @Schema(description = "创建时间", type = "LocalDateTime")
    private LocalDateTime updateTime;
    @Schema(description = "创建人", type = "Integer")
    private Integer createUser;
    @Schema(description = "修改人", type = "Integer")
    private Integer updateUser;
    @Schema(description = "版本号", type = "Integer", requiredMode = Schema.RequiredMode.REQUIRED, defaultValue = "0")
    private Integer version;
    @Schema(description = "备注", type = "String")
    private String remark;
}
