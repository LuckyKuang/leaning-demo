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

package com.luckykuang.model;

import com.luckykuang.enums.WordContentTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 标签内容
 * 对应 Word 模板标签内容如下：
 * 文本标签 {{value}}
 * 图片标签 {{@value}}
 * 表格标签 {{#value}}
 * 列表标签 {{*value}}
 * @author luckykuang
 * @date 2024/4/11 18:47
 */
@Data
@Accessors(chain = true)
public class LabelData {
    /**
     * 标签名称
     */
    private String labelName;
    /**
     * 文件内容类型
     */
    private WordContentTypeEnum typeEnum;
}
