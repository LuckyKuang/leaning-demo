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

package com.luckykuang.thread.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.ReadConverterContext;
import com.alibaba.excel.converters.WriteConverterContext;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.luckykuang.thread.enums.GenderEnum;

/**
 * 性别转换器
 * @author luckykuang
 * @date 2023/7/4 14:54
 */
public class GenderConverter implements Converter<Integer> {

    /**
     * Java字段需要转换的类型
     * @return 具体类型
     */
    @Override
    public Class<?> supportJavaTypeKey() {
        return Integer.class;
    }

    /**
     * Excel字段需要转换的类型
     * @return 具体类型
     */
    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    /**
     * Excel对象转Java对象
     * @param context read converter context
     */
    @Override
    public Integer convertToJavaData(ReadConverterContext<?> context) {
        return GenderEnum.convert(context.getReadCellData().getStringValue()).getValue();
    }

    /**
     * Java对象转Excel对象
     * @param context write context
     */
    @Override
    public WriteCellData<?> convertToExcelData(WriteConverterContext<Integer> context) {
        return new WriteCellData<>(GenderEnum.convert(context.getValue()).getDescription());
    }
}
