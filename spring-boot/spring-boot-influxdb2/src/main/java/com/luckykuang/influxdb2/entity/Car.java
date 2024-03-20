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

package com.luckykuang.influxdb2.entity;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import lombok.Data;

import java.time.Instant;

/**
 * @author luckykuang
 * @date 2023/12/12 10:53
 */
@Data
@Measurement(name = "car")
public class Car {
    /**
     * 主键：时间戳字段，带有索引，相当于主键
     */
    @Column(timestamp = true)
    private Instant time;
    /**
     * 名称：标签字段，带有索引
     */
    @Column(tag = true)
    private String name;
    /**
     * 经度：普通字段，无索引
     */
    @Column
    private Double longitude;
    /**
     * 纬度：普通字段，无索引
     */
    @Column
    private Double latitude;
}
