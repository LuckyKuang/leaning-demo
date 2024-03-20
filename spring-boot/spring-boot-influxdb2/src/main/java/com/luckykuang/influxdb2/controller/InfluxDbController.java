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

package com.luckykuang.influxdb2.controller;

import com.influxdb.client.domain.Bucket;
import com.luckykuang.influxdb2.entity.Car;
import com.luckykuang.influxdb2.service.InfluxDBService;
import com.luckykuang.influxdb2.vo.PointVO;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author luckykuang
 * @date 2023/12/12 11:20
 */
@Validated
@RestController
@RequestMapping("influxdb")
@RequiredArgsConstructor
public class InfluxDbController {

    private final InfluxDBService influxDBService;

    // 新建桶
    @PostMapping("bucket")
    public Bucket createBucket(String bucketName){
        return influxDBService.createBucket(bucketName);
    }
    // 查询桶
    @GetMapping("bucket")
    public Bucket queryBucket(String bucketName){
        return influxDBService.queryBucket(bucketName);
    }
    // 新建组织
    @PostMapping("org")
    public String createOrg(String orgName){
        influxDBService.createOrg(orgName);
        return "success";
    }
    // 查询组织
    @GetMapping("org")
    public String queryOrg(String orgId){
        influxDBService.queryOrg(orgId);
        return "success";
    }
    // 插入单条数据
    @PostMapping("msg")
    public String saveMsg(String bucketName, String orgName, PointVO pointVO){
        influxDBService.saveMsg(bucketName,orgName, pointVO);
        return "success";
    }
    // 插入多条数据
    @PostMapping("msg/list")
    public String saveMsgList(String bucketName, String orgName, List<PointVO> pointVOS){
        influxDBService.saveMsgList(bucketName,orgName, pointVOS);
        return "success";
    }
    // 删除数据
    @DeleteMapping("msg")
    public String delMsg(String startTime, String endTime, String bucketName, String orgName){
        influxDBService.delMsg(startTime,endTime,bucketName,orgName);
        return "success";
    }
    // 查询数据列表
    @GetMapping("msg")
    public List<Car> queryMsgList(String bucketName, String orgName, String startDate, String endDate){
        return influxDBService.queryMsgList(bucketName,orgName,startDate,endDate);
    }
}
