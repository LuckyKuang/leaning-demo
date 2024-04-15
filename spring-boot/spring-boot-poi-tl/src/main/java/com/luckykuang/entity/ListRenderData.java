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

package com.luckykuang.entity;

import com.deepoove.poi.data.NumbericRenderData;
import com.deepoove.poi.data.TextRenderData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.tuple.Pair;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STNumberFormat;

import java.util.List;

/**
 * 列表
 * @author luckykuang
 * @date 2024/4/11 18:48
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class ListRenderData extends LabelData {
    /**
     * 列表数据集
     */
    private List<TextRenderData> list;

    /**
     * 列表样式,支持罗马字符、有序无序等,默认为点
     */
    private Pair<STNumberFormat.Enum, String> pair = NumbericRenderData.FMT_BULLET;
}
