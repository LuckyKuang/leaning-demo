package com.luckykuang.redis.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求日志捕获
 * @author luckykuang
 * @since 2024/7/27 10:23
 */
@Aspect
@Component
public class WebReqLogAspect {

    private static final Logger logger = LoggerFactory.getLogger(WebReqLogAspect.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Pointcut("@annotation(com.luckykuang.redis.annotation.MarkReqLog)")
    public void markReqLogPointcut() {}

    /**
     * 前置通知 - 在接口上添加了 {@link com.luckykuang.redis.annotation.MarkReqLog} 注解的接口进行请求参数打印
     * @param joinPoint 切面
     */
    @Before("markReqLogPointcut()")
    public void logRequest(JoinPoint joinPoint) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest httpServletRequest = ((ServletRequestAttributes)requestAttributes).getRequest();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();

        Map<String, Object> paramMap = new HashMap<>();

        for (int i = 0; i < parameterAnnotations.length; i++) {
            for (Annotation annotation : parameterAnnotations[i]) {
                if (annotation instanceof RequestParam requestParam) {
                    paramMap.put(requestParam.value(), args[i]);
                } else if (annotation instanceof RequestBody) {
                    paramMap.put("body", args[i]);
                } else if (annotation instanceof RequestHeader requestHeader) {
                    paramMap.put(requestHeader.value(), args[i]);
                }
            }
            if (args[i] instanceof HttpServletRequest request) {
                Map<String, String[]> parameterMap = request.getParameterMap();
                for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                    paramMap.put(entry.getKey(), String.join(",", entry.getValue()));
                }
            }
        }

        // 记录请求参数
        try {
            String params = objectMapper.writeValueAsString(paramMap);
            logger.info("################THE REQUEST URL: [{}], CLASS: [{}#{}], IP: [{}], PARAMS: [{}]",
                    httpServletRequest.getRequestURL(), method.getDeclaringClass().getName(), method.getName(),httpServletRequest.getRemoteAddr(), params);
        } catch (Exception e) {
            logger.error("################THE REQUEST URL: [{}], CLASS: [{}#{}], IP: [{}]",
                    httpServletRequest.getRequestURL(), method.getDeclaringClass().getName(), method.getName(),httpServletRequest.getRemoteAddr(),e);
        }
    }
}
