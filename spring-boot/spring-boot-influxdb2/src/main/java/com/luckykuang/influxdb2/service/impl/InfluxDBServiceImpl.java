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

package com.luckykuang.influxdb2.service.impl;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.domain.*;
import com.influxdb.client.domain.Organization.StatusEnum;
import com.influxdb.client.write.Point;
import com.luckykuang.influxdb2.config.InfluxDbProperties;
import com.luckykuang.influxdb2.entity.Car;
import com.luckykuang.influxdb2.service.InfluxDBService;
import com.luckykuang.influxdb2.util.DateUtils;
import com.luckykuang.influxdb2.vo.PointVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * @author luckykuang
 * @date 2023/12/12 10:56
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InfluxDBServiceImpl implements InfluxDBService {

    private final InfluxDBClient influxDBClient;
    private final InfluxDbProperties properties;

    @Override
    public Bucket createBucket(String bucketName) {
        BucketRetentionRules rules = new BucketRetentionRules();
        // 持续时间(以秒为单位)表示数据将在数据库中保留多长时间。0表示无穷。最低:0
        rules.setEverySeconds(60 * 60 * 24 * 30);// 30天
        // 分片持续时间，以秒为单位。
        rules.setShardGroupDurationSeconds(60 * 60 * 24 * 30L);
        PostBucketRequest request = new PostBucketRequest();
        request.setOrgID(properties.getOrgId());
        request.setName(bucketName);
        request.setDescription(bucketName);
        request.setRetentionRules(List.of(rules));
        request.setSchemaType(SchemaType.IMPLICIT);
        return influxDBClient.getBucketsApi().createBucket(request);
    }

    @Override
    public Bucket queryBucket(String bucketName) {
        return influxDBClient.getBucketsApi().findBucketByName(bucketName);
    }

    @Override
    public Organization createOrg(String orgName) {
        Organization organization = new Organization();
        organization.setLinks(new OrganizationLinks());
        organization.setName(orgName);
        organization.setDescription(orgName);
        organization.setStatus(StatusEnum.ACTIVE);

        return influxDBClient.getOrganizationsApi().createOrganization(organization);
    }

    @Override
    public Organization queryOrg(String orgId) {
        return influxDBClient.getOrganizationsApi().findOrganizationByID(orgId);
    }

    @Override
    public void saveMsg(String bucketName, String orgName, PointVO pointVO) {
        Point point = Point
                .measurement("car")
                .addTags(pointVO.getTags())
                .addFields(pointVO.getFields())
                .time(Instant.now(), WritePrecision.NS);
        influxDBClient.makeWriteApi().writePoint(bucketName,orgName,point);
    }

    @Override
    public void saveMsgList(String bucketName, String orgName, List<PointVO> pointVOS) {
        List<Point> list = new ArrayList<>();
        for (PointVO pointVO : pointVOS) {
            Point point = Point
                    .measurement("car")
                    .addTags(pointVO.getTags())
                    .addFields(pointVO.getFields())
                    .time(Instant.now(), WritePrecision.NS);
            list.add(point);
        }
        influxDBClient.makeWriteApi().writePoints(bucketName,orgName,list);
    }

    @Override
    public void delMsg(String startTime, String endTime, String bucketName, String orgName) {
        DeletePredicateRequest request = new DeletePredicateRequest();
        request.start(OffsetDateTime.of(DateUtils.stringToLocalDateTime(startTime,"yyyy-MM-dd HH:mm:ss"),ZoneOffset.UTC));
        request.stop(OffsetDateTime.of(DateUtils.stringToLocalDateTime(endTime,"yyyy-MM-dd HH:mm:ss"),ZoneOffset.UTC));
        influxDBClient.getDeleteApi().delete(request,bucketName,orgName);
    }

    @Override
    public List<Car> queryMsgList(String bucketName,String orgName,String startDate,String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        LocalDate now = LocalDate.now();

        String query = "from(bucket: \""+bucketName+"\")" +
                " |> range(start: -"+start.until(now, ChronoUnit.DAYS)+"d, stop: "+now.until(end, ChronoUnit.DAYS)+"d)";
        return influxDBClient.getQueryApi().query(query, orgName, Car.class);
    }
}
