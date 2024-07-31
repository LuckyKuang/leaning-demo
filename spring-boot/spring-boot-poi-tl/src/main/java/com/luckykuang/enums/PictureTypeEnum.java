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

package com.luckykuang.enums;

import lombok.Getter;

/**
 * 图片类型支持格式枚举
 * @author luckykuang
 * @date 2024/4/15 10:07
 */
@Getter
public enum PictureTypeEnum {
    /**
     * png图片
     */
    PNG(".png"),
    /**
     * JPG图片
     */
    JPG(".jpg"),
    /**
     * jpeg
     */
    JPEG(".jpeg");

    private final String picName;

    PictureTypeEnum(String picName) {
        this.picName = picName;
    }
}
