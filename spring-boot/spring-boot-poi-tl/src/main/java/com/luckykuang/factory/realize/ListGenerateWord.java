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

package com.luckykuang.factory.realize;

import com.deepoove.poi.data.NumbericRenderData;
import com.luckykuang.enums.WordContentTypeEnum;
import com.luckykuang.factory.GenerateWord;
import com.luckykuang.factory.GenerateWordFactory;
import com.luckykuang.model.LabelData;
import com.luckykuang.model.ListSeriesRenderData;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

/**
 * 列表类型
 * @author luckykuang
 * @date 2024/4/15 10:19
 */
@Component
public class ListGenerateWord implements GenerateWord {

    @PostConstruct
    private void init(){
        GenerateWordFactory.register(WordContentTypeEnum.LIST,this);
    }

    @Override
    public Object generateWord(LabelData data) {
        ListSeriesRenderData listData =  (ListSeriesRenderData) data;
        return new NumbericRenderData(listData.getPair(),listData.getList());
    }
}
