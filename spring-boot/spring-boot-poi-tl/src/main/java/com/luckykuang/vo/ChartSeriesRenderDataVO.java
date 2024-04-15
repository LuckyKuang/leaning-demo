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

package com.luckykuang.vo;

import com.deepoove.poi.data.SeriesRenderData;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author luckykuang
 * @date 2024/4/11 18:49
 */
@Data
@Accessors(chain = true)
public class ChartSeriesRenderDataVO {
    /**
     * 系列名称
     */
    private String renderTitle;
    /**
     * 系列对应的数据
     */
    private Number[] data;
    /**
     * 该系列对应生成的图表类型
     */
    private SeriesRenderData.ComboType comboType = null;
}
