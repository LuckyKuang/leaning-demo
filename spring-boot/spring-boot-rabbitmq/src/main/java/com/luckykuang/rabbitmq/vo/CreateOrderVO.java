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

package com.luckykuang.rabbitmq.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author luckykuang
 * @date 2023/9/8 14:11
 */
@Data
@Schema(description = "创建订单")
public class CreateOrderVO {

    @Schema(description = "所购买图书id", requiredMode = Schema.RequiredMode.REQUIRED, type = "Long", example = "1008")
    private Long bookId;

    @Schema(description = "所购买图书名称", requiredMode = Schema.RequiredMode.REQUIRED, type = "String", example = "红楼梦")
    private String bookName;

    @Schema(description = "图书价格", hidden = true)
    private BigDecimal price;

    @Schema(description = "订单编号", hidden = true)
    private String orderNo;
}
