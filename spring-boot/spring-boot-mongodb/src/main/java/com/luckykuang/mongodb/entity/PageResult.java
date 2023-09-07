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

package com.luckykuang.mongodb.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 分页结果
 * @author luckykuang
 * @date 2023/8/18 15:47
 */
@Data
@Schema(description = "分页结果")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageResult<T> {

    @Schema(description = "页码，从1开始")
    private Integer pageNum;

    @Schema(description = "页面大小")
    private Integer pageSize;

    @Schema(description = "总数")
    private Long total;

    @Schema(description = "总页数")
    private Integer pages;

    @Schema(description = "数据")
    private List<T> list;
}
