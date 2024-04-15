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

package com.luckykuang.realize;

import com.deepoove.poi.data.ChartMultiSeriesRenderData;
import com.deepoove.poi.data.ChartSingleSeriesRenderData;
import com.deepoove.poi.data.SeriesRenderData;
import com.luckykuang.entity.ChartSeriesRenderData;
import com.luckykuang.entity.LabelData;
import com.luckykuang.enums.WordContentTypeEnum;
import com.luckykuang.factory.GenerateWordFactory;
import com.luckykuang.service.GenerateWord;
import com.luckykuang.vo.ChartSeriesRenderDataVO;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 图表类型
 * @author luckykuang
 * @date 2024/4/15 10:17
 */
@Component
public class ChartGenerateWord implements GenerateWord {
    @PostConstruct
    private void init(){
        GenerateWordFactory.register(WordContentTypeEnum.CHART,this);
    }
    @Override
    public Object generateWord(LabelData obj) {
        ChartSeriesRenderData renderData  = (ChartSeriesRenderData) obj;
        if (Objects.nonNull(renderData.getCharType()) && Objects.equals("Single",renderData.getCharType().getType())){
            ChartSingleSeriesRenderData singleSeriesRenderData = new ChartSingleSeriesRenderData();
            singleSeriesRenderData.setCategories(renderData.getCategories());
            singleSeriesRenderData.setChartTitle(renderData.getTitle());
            ChartSeriesRenderDataVO seriesData = renderData.getSenderData().get(0);
            SeriesRenderData srd = new SeriesRenderData(seriesData.getRenderTitle(),seriesData.getData());
            if (Objects.nonNull(seriesData.getComboType())){
                srd.setComboType(seriesData.getComboType());
            }
            singleSeriesRenderData.setSeriesData(srd);
            return singleSeriesRenderData;
        } else {
            ChartMultiSeriesRenderData seriesRenderData = new ChartMultiSeriesRenderData();
            seriesRenderData.setCategories(renderData.getCategories());
            seriesRenderData.setChartTitle(renderData.getTitle());
            List<ChartSeriesRenderDataVO> renderDataList = renderData.getSenderData();
            List<SeriesRenderData> groupData = new ArrayList<>();
            renderDataList.forEach(data -> {
                SeriesRenderData srd = new SeriesRenderData(data.getRenderTitle(),data.getData());
                if (Objects.nonNull(data.getComboType())){
                    srd.setComboType(data.getComboType());
                }
                groupData.add(srd);
            });
            seriesRenderData.setSeriesDatas(groupData);
            return seriesRenderData;
        }
    }
}
