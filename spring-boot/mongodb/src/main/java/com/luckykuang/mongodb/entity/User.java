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

import com.luckykuang.mongodb.valid.SaveGroup;
import com.luckykuang.mongodb.valid.UpdateGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author luckykuang
 * @date 2023/8/18 10:33
 */
@Data
@Schema(description = "用户表")
public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 7478513151262531440L;

    @Schema(description = "id")
    @NotBlank(groups = {UpdateGroup.class})
    private String id;

    @NotBlank(groups = {SaveGroup.class,UpdateGroup.class})
    @Schema(description = "用户名称")
    private String name;

    @NotBlank(groups = {SaveGroup.class,UpdateGroup.class})
    @Schema(description = "用户年龄")
    private Integer age;
}
