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

import com.luckykuang.enums.ChartsCombinationTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 图表系列渲染数据
 * @author luckykuang
 * @date 2024/4/11 18:48
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class ChartsSeriesRenderData extends LabelData {
    /**
     * 横轴数据
     */
    private String[] categories;

    /**
     * 图表名称
     */
    private String title;

    /**
     * 图表类型 组合
     */
    private ChartsCombinationTypeEnum charType = ChartsCombinationTypeEnum.MULTI;

    /**
     * 系列对应数据
     */
    private List<ChartsSeriesRenderDataItem> senderData;
}
