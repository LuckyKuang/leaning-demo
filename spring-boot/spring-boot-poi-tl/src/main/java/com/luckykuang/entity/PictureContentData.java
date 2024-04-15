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

import com.luckykuang.enums.PicTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.File;

/**
 * 图片
 * @author luckykuang
 * @date 2024/4/11 18:48
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class PictureContentData extends LabelData {
    /**
     * 图片宽度
     */
    private Integer width;
    /**
     * 图片高度
     */
    private Integer height;
    /**
     * 图片类型
     */
    private PicTypeEnum picType;
    /**
     * 图片地址（网络图片插入时使用）
     */
    private String picUrl;
    /**
     * 图片文件
     */
    private File file;
}
