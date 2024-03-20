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

import com.influxdb.client.domain.Bucket;
import com.influxdb.client.domain.Organization;
import com.luckykuang.influxdb2.entity.Car;
import com.luckykuang.influxdb2.vo.PointVO;

import java.util.List;

/**
 * @author luckykuang
 * @date 2023/12/12 11:26
 */
public interface InfluxDBService {
    Bucket createBucket(String bucketName);

    Bucket queryBucket(String bucketName);

    Organization createOrg(String orgName);

    Organization queryOrg(String orgId);

    void saveMsg(String bucketName, String orgName, PointVO pointVO);

    void saveMsgList(String bucketName, String orgName, List<PointVO> pointVOS);

    void delMsg(String startTime, String endTime, String bucketName, String orgName);

    List<Car> queryMsgList(String bucketName, String orgName, String startDate, String endDate);
}
