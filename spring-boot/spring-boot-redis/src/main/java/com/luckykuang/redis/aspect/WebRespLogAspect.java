package com.luckykuang.redis.aspect;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

/**
 * 响应日志捕获
 * @author luckykuang
 * @since 2024/7/27 10:20
 */
@Aspect
@Component
public class WebRespLogAspect {

    private static final Logger logger = LoggerFactory.getLogger(WebRespLogAspect.class);

    @Pointcut("@annotation(com.luckykuang.redis.annotation.MarkRespLog)")
    public void markRespLogPointcut() {}

    /**
     * 后置通知 - 在接口上添加了 {@link com.luckykuang.redis.annotation.MarkRespLog} 注解的接口进行返回参数打印
     * @param joinPoint 切面
     * @param result    响应参数打印
     */
    @AfterReturning(pointcut = "markRespLogPointcut()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            logger.warn("RequestAttributes is null, cannot log response");
            return;
        }
        HttpServletRequest httpServletRequest = ((ServletRequestAttributes)requestAttributes).getRequest();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        logger.info("################THE RESPONSE URL: [{}], CLASS: [{}#{}], IP: [{}], RESULT_DATA: [{}]",
                httpServletRequest.getRequestURL(), method.getDeclaringClass().getName(), method.getName(),httpServletRequest.getRemoteAddr(), result);
    }
}
