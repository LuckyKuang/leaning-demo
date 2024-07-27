package com.luckykuang.redis.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
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
 * 全局日志捕获
 * @author luckykuang
 * @since 2024/7/11 14:53
 */
@Aspect
@Component
public class WebGlobalLogAspect {

    private static final Logger logger = LoggerFactory.getLogger(WebGlobalLogAspect.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Pointcut("execution(public * com.luckykuang.redis.controller..*.*(..))")
    public void controllerLog(){}

    /**
     * 环绕通知
     * @param joinPoint 切面
     */
    @Around("controllerLog()")
    public Object logControllerMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            logger.warn("RequestAttributes is null, cannot log request and response");
            return joinPoint.proceed();
        }
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

        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long end = System.currentTimeMillis();

        // 记录返回结果
        try {
            String response = objectMapper.writeValueAsString(result);
            logger.info("################THE RESPONSE URL: [{}], CLASS: [{}#{}], IP: [{}], time(ms): [{}], RESULT_DATA: [{}]",
                    httpServletRequest.getRequestURL(), method.getDeclaringClass().getName(), method.getName(),httpServletRequest.getRemoteAddr(), end - start, response);
        } catch (Exception e) {
            logger.error("################THE RESPONSE URL: [{}], CLASS: {}#{}, IP: [{}], time(ms): [{}]",
                    httpServletRequest.getRequestURL(), method.getDeclaringClass().getName(), method.getName(),httpServletRequest.getRemoteAddr(),end - start,e);
        }

        return result;
    }
}
