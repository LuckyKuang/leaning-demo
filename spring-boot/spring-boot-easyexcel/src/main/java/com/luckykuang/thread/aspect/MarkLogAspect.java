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

package com.luckykuang.thread.aspect;

import com.alibaba.fastjson.JSON;
import com.luckykuang.thread.entity.LogInfo;
import com.luckykuang.thread.service.LogInfoService;
import com.luckykuang.thread.utils.ThreadPoolUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 日志记录注解实现
 * @author luckykuang
 * @date 2023/11/13 10:33
 */
@Aspect
@Component
public class MarkLogAspect {

    @Resource
    private LogInfoService logInfoService;

    @Pointcut("@annotation(com.luckykuang.thread.aspect.MarkLog)")
    public void pointcut() {

    }

    @Around("pointcut()")
    public Object aroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        LogInfo info = new LogInfo();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        info.setMethodName(method.getName());
        Annotation[] annotations = method.getDeclaredAnnotations();
        String methodType = null;
        // 获取方法类型
        for (Annotation annotation : annotations) {
            String name = annotation.annotationType().getName();
            if (name.contains("GetMapping")){
                methodType = "GET";
                break;
            }
            else if (name.contains("PostMapping")){
                methodType = "POST";
                break;
            }
            else if (name.contains("PutMapping")){
                methodType = "PUT";
                break;
            }
            else if (name.contains("DeleteMapping")){
                methodType = "DELETE";
                break;
            }
        }
        info.setMethodType(methodType);
        // 获得当前登录用户，根据自己的实际业务设置
        info.setCreateUser("admin");
        MarkLog annotation = method.getAnnotation(MarkLog.class);
        Object[] args = joinPoint.getArgs();
        if (args != null){
            StringBuilder sb1 = new StringBuilder();
            StringBuilder sb2 = new StringBuilder();
            for (Object obj : args) {
                if (obj instanceof HttpServletRequest){
                    // 暂时不考虑HttpServletRequest
                    continue;
                }
                if (obj instanceof HttpServletResponse){
                    // 暂时不考虑HttpServletResponse
                    continue;
                }
                if (obj instanceof MultipartFile file){
                    sb1.append(file.getOriginalFilename());
                    sb1.append("|");
                    continue;
                }
                sb2.append(JSON.toJSONString(obj));
                sb2.append("|");
            }
            if (!sb1.isEmpty()) {
                // 删除最后一个符号
                sb1.deleteCharAt(sb1.length() - 1);
                info.setParams(sb1.toString());
            }
            if (!sb2.isEmpty()) {
                // 删除最后一个符号
                sb2.deleteCharAt(sb2.length() - 1);
                info.setParams(sb2.toString());
            }

        }
        info.setMethodDesc(annotation.desc());
        Object result = null;
        try {
            result = joinPoint.proceed();
            if (result != null) {
                info.setResponse(JSON.toJSONString(result));
            }
            info.setStatus("success");
        } catch (Throwable e) {
            info.setResponse(result == null ? null : JSON.toJSONString(result));
            info.setStatus("failure");
            info.setErrorDesc(e.getMessage());
            throw e;
        } finally {
            info.setCreateTime(LocalDateTime.now());
            // 保存日志
            ThreadPoolUtils.execute(() -> logInfoService.save(info));
        }
        return result;
    }
}
