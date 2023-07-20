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

package com.luckykuang.order.delayed.service.impl;

import com.luckykuang.order.delayed.delayed.CancelOrderDelayed;
import com.luckykuang.order.delayed.service.CancelOrderDelayedService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author luckykuang
 * @date 2023/7/14 14:18
 */
@Slf4j
@Service
public class CancelOrderDelayedServiceImpl implements CancelOrderDelayedService {

    @Override
    public void cancelOrder() {
        DelayQueue<CancelOrderDelayed> queue = new DelayQueue<>();
        for (int i = 0; i < 5; i++) {
            // 生成订单，10秒超时
            CancelOrderDelayed cancelOrder = new CancelOrderDelayed(
                    "orderNo100" + i,
                    TimeUnit.NANOSECONDS.convert(10, TimeUnit.SECONDS)
            );
            String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            log.info(time + ":生成了订单，10秒有效期，order：" + cancelOrder);
            queue.put(cancelOrder);
            // 每1秒生成一个订单
            try {
                TimeUnit.SECONDS.sleep(1);
            }catch (InterruptedException e){
                log.error("系统异常",e);
            }
        }
        try {
            while (!queue.isEmpty()) {
                CancelOrderDelayed order = queue.take();
                String timeout = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
               log.info(timeout + ":订单超时，order：" + order);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
