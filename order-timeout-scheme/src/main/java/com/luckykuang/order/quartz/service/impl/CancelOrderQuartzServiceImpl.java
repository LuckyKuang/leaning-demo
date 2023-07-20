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

package com.luckykuang.order.quartz.service.impl;

import com.luckykuang.order.quartz.job.CancelOrderJob;
import com.luckykuang.order.quartz.service.CancelOrderQuartzService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.stereotype.Service;

/**
 * @author luckykuang
 * @date 2023/7/14 10:20
 */
@Slf4j
@Service
public class CancelOrderQuartzServiceImpl implements CancelOrderQuartzService {

    @Override
    public void cancelOrder() {
        // 创建任务详情
        JobDetail jobDetail = JobBuilder.newJob(CancelOrderJob.class).build();
        // 创建触发器
        Trigger trigger = TriggerBuilder.newTrigger()
                // 创建一个定时任务，时间粒度：每秒执行一次，无限循环
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(1).repeatForever())
                .build();
        try {
            // 创建一个任务调度
            Scheduler scheduler = new StdSchedulerFactory().getScheduler();
            scheduler.scheduleJob(jobDetail, trigger);
            scheduler.start();
        } catch (SchedulerException e){
            log.error("定时退款任务执行异常",e);
        }
    }
}
