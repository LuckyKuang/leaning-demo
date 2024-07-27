package com.luckykuang.redis.util;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义异常工具类
 * @author luckykuang
 * @since 2024/7/8 17:04
 */
@Slf4j
public class BusinessExceptionUtils {

    private BusinessExceptionUtils() {}

    /**
     * 自定义异常打印
     * @param e 异常信息
     */
    public static void getErrorMessage(Exception e) {
        StackTraceElement[] stackTrace = e.getStackTrace();
        List<String> stackTraceList = new ArrayList<>();
        for (StackTraceElement stackTraceElement : stackTrace) {
            String stackTraceMsg = stackTraceElement.toString();
            // 对异常详情进行换行
            stackTraceList.add(stackTraceMsg + "\n");
        }
        log.error("异常类型：{},异常信息：{},异常详情：{}",e.getClass().getName(),e.getMessage(),stackTraceList);
    }
}
