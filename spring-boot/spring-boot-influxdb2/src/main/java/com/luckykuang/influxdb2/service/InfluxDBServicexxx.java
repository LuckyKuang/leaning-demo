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

package com.luckykuang.influxdb2.service;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.domain.DeletePredicateRequest;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import com.luckykuang.influxdb2.config.InfluxDbProperties;
import com.luckykuang.influxdb2.util.InfluxDBFluxUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author luckykuang
 * @date 2023/12/12 10:23
 */
@Slf4j
@Service
public class InfluxDBServicexxx {
    @Autowired
    private InfluxDBClient influxDBClient;

    @Autowired
    private InfluxDbProperties properties;

    /**
     * 批量数据写入
     */
    public void write(){
        List<Point> list = new ArrayList<>();
        Point point = Point
                .measurement("mem")
                .addTag("host", "host1")
                .addField("used_percent", 23.43234543)
                .time(Instant.now(), WritePrecision.NS);
        list.add(point);
        influxDBClient.getWriteApi().writePoints(list);
    }


    /**
     * 单条数据写入
     */
    public void write2(){
        List<Point> list = new ArrayList<>();
        Point point = Point
                .measurement("mem")
                .addTag("host", "host1")
                .addField("used_percent", 23.43234543)
                .time(Instant.now(), WritePrecision.NS);
        list.add(point);
        influxDBClient.getWriteApi().writePoint(point);
    }
    /**
     * 删除数据
     */
    public void delete() {
        DeletePredicateRequest deletePredicateRequest = new DeletePredicateRequest();
        deletePredicateRequest.start(LocalDateTime.now().atOffset(ZoneOffset.ofHours(0)).minusDays(5));
        deletePredicateRequest.stop(LocalDateTime.now().atOffset(ZoneOffset.ofHours(0)));

        influxDBClient.getDeleteApi().delete(deletePredicateRequest, properties.getBucketName(), properties.getOrgId());
    }

    /**
     *查询数据
     */
    public void select(){
        StringBuffer stringBuilder = new StringBuffer();
//        InfluxDBFluxExpression.appendCommonFlux(stringBuilder, properties.getBucket(), sn, DateUtils.UTCTime(start), DateUtils.UTCTime(stop));
//        InfluxDBFluxExpression.appendTagFlux(stringBuilder, map.get("sn").toString());
        InfluxDBFluxUtils.appendTimeShiftFlux(stringBuilder);
        log.info("查询sql :{}", stringBuilder.toString());
        // 通过时间分组  查询时间段的数据
        List<FluxTable> tables = influxDBClient.getQueryApi().query(stringBuilder.toString());
        List<Map<String, Object>> list = new ArrayList<>();
        for (FluxTable table : tables) {
            List<FluxRecord> records = table.getRecords();
            for (FluxRecord record : records) {
                log.info("{}---{}---{}---{}", record.getMeasurement(),record.getField(),record.getValue(),record.getTime());
            }
        }
    }
}
