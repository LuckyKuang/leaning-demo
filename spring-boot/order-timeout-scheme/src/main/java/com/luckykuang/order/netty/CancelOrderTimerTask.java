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

package com.luckykuang.order.netty;

import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * @author luckykuang
 * @date 2023/7/14 15:07
 */
@Slf4j
public class CancelOrderTimerTask implements TimerTask {

    private final String orderNo;

    public CancelOrderTimerTask(String orderNo) {
        this.orderNo = orderNo;
    }

    @Override
    public void run(Timeout timeout) throws Exception {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        log.info(time + "：处理订单超时，orderNo：" + orderNo);
    }

    public static void main(String[] args) {
        /*
        此处使用的HashedWheelTimer构造方法参数解释如下：
        threadFactory：创建线程的工厂
        tickDuration：时间间隔，这里的1，结合后面的TimeUnit.SECONDS,就是走完一格需要1秒时间。
        unit：时间单位，这是里秒
        ticksPerWheel：表示数组的大小，也就是格子的多少
         */
        HashedWheelTimer hashedWheelTimer = new HashedWheelTimer(new DefaultThreadFactory("test-thread"), 1, TimeUnit.SECONDS, 60);
        hashedWheelTimer.start();
        CancelOrderTimerTask timerTask0 = new CancelOrderTimerTask("orderNo1000");
        CancelOrderTimerTask timerTask1 = new CancelOrderTimerTask("orderNo1001");
        CancelOrderTimerTask timerTask2 = new CancelOrderTimerTask("orderNo1002");
        CancelOrderTimerTask timerTask3 = new CancelOrderTimerTask("orderNo1003");
        hashedWheelTimer.newTimeout(timerTask0, 0, TimeUnit.SECONDS);
        hashedWheelTimer.newTimeout(timerTask1, 5, TimeUnit.SECONDS);
        hashedWheelTimer.newTimeout(timerTask2, 30, TimeUnit.SECONDS);
        hashedWheelTimer.newTimeout(timerTask3, 70, TimeUnit.SECONDS);
    }
}
