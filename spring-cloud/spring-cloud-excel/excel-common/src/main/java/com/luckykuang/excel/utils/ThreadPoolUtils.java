package com.luckykuang.excel.utils;

import com.zaxxer.hikari.util.UtilityElf;

import java.util.concurrent.*;

/**
 * @author luckykuang
 * @since 2025/2/11 16:54
 */
public class ThreadPoolUtils {
    private ThreadPoolUtils() {}

    private static final int FULL_PROCESSORS = 10;

    /**
     * 全局线程池
     */
    public static final ExecutorService EXECUTOR_SERVICE = new ThreadPoolExecutor(
            FULL_PROCESSORS,// 核心线程数
            FULL_PROCESSORS,// 最大线程数
            0L,// 空闲线程保活时间。当(核心线程数 = 最大线程数)时，保活时间设置没有任何意义
            TimeUnit.SECONDS,// 线程保活时间单位
            new LinkedBlockingDeque<>(FULL_PROCESSORS * 1000),// 线程阻塞队列
            new UtilityElf.DefaultThreadFactory("full-thread-pool-%d",false),// 线程创建工厂
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
     * @param coreThreads       核心线程数
     * @param maxThreads        最大线程数
     * @param keepAliveSeconds  保活时间(秒)
     * @param queueSize         队列大小
     * @return ThreadPoolExecutor
     */
    public static ThreadPoolExecutor getThreadPoolExecutor(Integer coreThreads, Integer maxThreads,Long keepAliveSeconds,Integer queueSize) {
        return new ThreadPoolExecutor(
                coreThreads == null ? AVAILABLE_PROCESSORS : coreThreads,// 核心线程数
                maxThreads == null ? AVAILABLE_PROCESSORS : maxThreads,// 最大线程数
                keepAliveSeconds == null ? 0L : keepAliveSeconds,// 空闲线程保活时间。当(核心线程数 = 最大线程数)时，保活时间设置没有任何意义
                TimeUnit.SECONDS,// 线程保活时间单位
                new LinkedBlockingDeque<>(queueSize == null ? AVAILABLE_PROCESSORS * 10 : queueSize),// 线程阻塞队列
                new UtilityElf.DefaultThreadFactory("custom-thread-pool-%d",false),// 线程创建工厂
                new ThreadPoolExecutor.AbortPolicy()// 线程拒绝策略
        );
    }
}
