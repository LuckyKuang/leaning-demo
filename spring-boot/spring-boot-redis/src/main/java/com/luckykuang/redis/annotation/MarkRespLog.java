package com.luckykuang.redis.annotation;

import java.lang.annotation.*;

/**
 * 拦截接口响应数据注解
 * @author luckykuang
 * @since 2024/7/11 14:52
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MarkRespLog {
}
