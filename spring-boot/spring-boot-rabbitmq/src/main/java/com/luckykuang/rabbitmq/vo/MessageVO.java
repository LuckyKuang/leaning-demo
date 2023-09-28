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

import java.io.Serial;
import java.io.Serializable;

/**
 * @author luckykuang
 * @date 2023/9/8 11:40
 */
@Data
@Schema(description = "消息类")
public class MessageVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 507751532457628968L;

    @Schema(description = "消息id")
    private String id;

    @Schema(description = "消息内容", type = "String", requiredMode = Schema.RequiredMode.REQUIRED, example = "这是一个测试过期时间为3分钟的消息")
    private String content;

    @Schema(description = "过期时间", type = "Long", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long ttl;
}
