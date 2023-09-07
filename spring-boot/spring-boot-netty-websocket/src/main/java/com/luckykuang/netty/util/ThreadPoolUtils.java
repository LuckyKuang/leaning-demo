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

package com.luckykuang.netty.util;

import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.concurrent.*;

/**
 * 自定义线程池配置
 *
 * 拒绝策略记忆技巧：
 * CallerRunsPolicy
 *      官方解释：它直接在execute方法的调用线程中运行被拒绝的任务，除非执行程序已被关闭，在这种情况下，任务将被丢弃。
 *      大白话：线程池没有空闲线程/阻塞队列塞满时，谁调用谁执行。例如：主线程调用了线程池执行A任务，那主线程就自己去执行该A任务
 * AbortPolicy
 *      官方解释：该处理程序将抛出RejectedExecutionException。这是ThreadPoolExecutor和ScheduledThreadPoolExecutor的默认处理程序。
 *      大白话：线程池没有空闲线程/阻塞队列塞满时，直接抛出异常，不再执行新任务，除非线程再次空闲出来。
 * DiscardPolicy
 *      官方解释：它以静默方式丢弃被拒绝的任务。
 *      大白话：线程池没有空闲线程/阻塞队列塞满时，不会抛异常，但是会直接丢弃该新任务，相当于此次任务被忽略了。
 * DiscardOldestPolicy
 *      官方解释：它丢弃最老的未处理请求，然后重试执行，除非执行程序被关闭，在这种情况下，任务被丢弃。
 *      大白话：线程池没有空闲线程/阻塞队列塞满时，也不会抛异常，但它会丢弃掉阻塞队列中排队最久的那个任务，排队最久的那个任务还没来得及执行就被忽略了。
 *
 * @author luckykuang
 * @date 2023/7/10 16:34
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ThreadPoolUtils {

    private static final int FULL_PROCESSORS = 100;

    /**
     * 全局线程池
     */
    private static final ExecutorService EXECUTOR_SERVICE = new ThreadPoolExecutor(
            FULL_PROCESSORS,// 核心线程数
            FULL_PROCESSORS,// 最大线程数
            0L,// 空闲线程保活时间。当(核心线程数 = 最大线程数)时，保活时间设置没有任何意义
            TimeUnit.SECONDS,// 线程保活时间单位
            new LinkedBlockingDeque<>(FULL_PROCESSORS * 10),// 线程阻塞队列
            new DefaultThreadFactory("full-thread-pool-%d"),// 线程创建工厂
            new ThreadPoolExecutor.CallerRunsPolicy()// 线程拒绝策略
    );

    private static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();

    /**
     * 异步无回调
     */
    public static void execute(Runnable runnable) {
        EXECUTOR_SERVICE.execute(runnable);
    }

    /**
     * 异步有回调
     */
    public static <T> Future<T> submit(Callable<T> callable) {
        return EXECUTOR_SERVICE.submit(callable);
    }

    /**
     * 自定义配置的线程池，用于局部代码获取，用完记得关闭
     */
    public static ThreadPoolExecutor getThreadPoolExecutor() {
        return new ThreadPoolExecutor(
                AVAILABLE_PROCESSORS,// 核心线程数
                AVAILABLE_PROCESSORS,// 最大线程数
                0L,// 空闲线程保活时间。当(核心线程数 = 最大线程数)时，保活时间设置没有任何意义
                TimeUnit.SECONDS,// 线程保活时间单位
                new LinkedBlockingDeque<>(AVAILABLE_PROCESSORS * 10),// 线程阻塞队列
                new DefaultThreadFactory("full-thread-pool-%d"),// 线程创建工厂
                new ThreadPoolExecutor.AbortPolicy()// 线程拒绝策略
        );
    }
}
