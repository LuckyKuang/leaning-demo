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

import com.luckykuang.entity.LabelData;
import com.luckykuang.entity.TextContentData;
import com.luckykuang.enums.WordContentTypeEnum;
import com.luckykuang.factory.GenerateWordFactory;
import com.luckykuang.service.GenerateWord;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author luckykuang
 * @date 2024/4/15 10:20
 */
@Component
public class TextGenerateWord implements GenerateWord {
    @PostConstruct
    public void init(){
        GenerateWordFactory.register(WordContentTypeEnum.TEXT,this);
    }

    @Override
    public Object generateWord(LabelData data) {
        TextContentData contentData = (TextContentData) data;
        return Objects.nonNull(contentData.getLinkData()) ? contentData.getLinkData() :
                Objects.nonNull(contentData.getRenderData()) ? contentData.getRenderData() : contentData.getContent();
    }
}
