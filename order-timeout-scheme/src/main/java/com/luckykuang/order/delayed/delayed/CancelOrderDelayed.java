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

package com.luckykuang.order.delayed.delayed;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * @author luckykuang
 * @date 2023/7/14 13:55
 */
public class CancelOrderDelayed implements Delayed {

    private final String orderNo;

    private final long timeout;

    public CancelOrderDelayed(String orderNo, long timeout) {
        this.orderNo = orderNo;
        this.timeout = timeout + System.nanoTime();
    }

    /**
     * 在给定的时间单位内返回与此对象关联的剩余延迟
     * @param unit the time unit
     * @return
     */
    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(timeout - System.nanoTime(), TimeUnit.NANOSECONDS);
    }

    /**
     * 将此对象与指定对象进行顺序比较。当此对象小于、等于或大于指定对象时，返回负整数、零或正整数。
     * @param o the object to be compared.
     * @return
     */
    @Override
    public int compareTo(Delayed o) {
        if (o == this) {
            return 0;
        }
        CancelOrderDelayed other = (CancelOrderDelayed) o;
        long delayDifference = (getDelay(TimeUnit.NANOSECONDS) - other.getDelay(TimeUnit.NANOSECONDS));
        return (delayDifference == 0) ? 0 : ((delayDifference < 0) ? -1 : 1);
    }
}
