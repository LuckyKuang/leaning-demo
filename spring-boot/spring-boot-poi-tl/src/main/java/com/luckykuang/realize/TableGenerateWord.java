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

import com.deepoove.poi.data.MiniTableRenderData;
import com.deepoove.poi.data.RowRenderData;
import com.luckykuang.entity.LabelData;
import com.luckykuang.entity.TableSeriesRenderData;
import com.luckykuang.enums.WordContentTypeEnum;
import com.luckykuang.factory.GenerateWordFactory;
import com.luckykuang.service.GenerateWord;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author luckykuang
 * @date 2024/4/15 10:20
 */
@Component
public class TableGenerateWord implements GenerateWord {
    @PostConstruct
    private void init(){
        GenerateWordFactory.register(WordContentTypeEnum.TABLE,this);
    }
    @Override
    public Object generateWord(LabelData data) {
        TableSeriesRenderData tableData = (TableSeriesRenderData) data;
        RowRenderData header = RowRenderData.build(tableData.getHeader());
        List<RowRenderData> contentData = new ArrayList<>();
        tableData.getContents().forEach(con ->{
            contentData.add(RowRenderData.build(con));
        });
        return new MiniTableRenderData(header,contentData);
    }
}
