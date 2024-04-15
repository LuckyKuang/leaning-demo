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

package com.luckykuang.factory;

import com.luckykuang.enums.WordContentTypeEnum;
import com.luckykuang.service.GenerateWord;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 生成word工厂
 * @author luckykuang
 * @date 2024/4/15 10:16
 */
public class GenerateWordFactory {
    private static final Map<WordContentTypeEnum, GenerateWord> TYPE_BACK_DATA = new HashMap<>();

    public static void register(WordContentTypeEnum typeEnum, GenerateWord word){
        if (Objects.nonNull(typeEnum)){
            TYPE_BACK_DATA.put(typeEnum,word);
        }
    }

    public static GenerateWord getBackData(WordContentTypeEnum typeEnum){
        return TYPE_BACK_DATA.get(typeEnum);
    }
}
