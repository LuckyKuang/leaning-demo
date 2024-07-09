package com.luckykuang.redis.config;

import com.luckykuang.redis.base.ApiResult;
import com.luckykuang.redis.enums.ErrorCode;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;

import java.util.stream.Collectors;

/**
 * 全局异常处理类
 * @author luckykuang
 * @since 2024/7/9 9:40
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandle {

    /**
     * 处理方法上 @Valid/@Validated 校验不通过产生的异常
     * <br/>如果自定义消息添加"custom_"前缀，将会只输出自定义的message
     * <br/>否则默认输出fieldName + message
     */
    @ResponseBody
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ApiResult<?> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> {
                    String defaultMessage = fieldError.getDefaultMessage();
                    String fieldName = fieldError.getField();
                    if (defaultMessage.startsWith("custom_")){
                        return defaultMessage.replace("custom_","");
                    }
                    return fieldName + defaultMessage;
                }).collect(Collectors.joining(";"));
        log.warn("[methodArgumentNotValidExceptionHandler] msg:{}", message);
        return ApiResult.failed(1000,message);
    }

    /**
     * 处理类上 @Validated 校验不通过产生的异常
     * <br/>如果有自定义消息，将会直接输出自定义customMessage
     * <br/>否则直接输出默认fieldName + defaultMessage
     */
    @ResponseBody
    @ExceptionHandler(value = ConstraintViolationException.class)
    public ApiResult<?> constraintViolationExceptionHandler(ConstraintViolationException e, HandlerMethod handlerMethod) {
        String message = e.getConstraintViolations().stream()
                .map(violation -> {
                    String customMessage = violation.getMessageTemplate();
                    String defaultMessage = violation.getMessage();
                    if (customMessage.equals(defaultMessage)) {
                        return customMessage;
                    }
                    String fieldName = violation.getPropertyPath().toString();
                    String arg = fieldName.substring(fieldName.lastIndexOf("arg") + 3);
                    int index = Integer.parseInt(arg);
                    MethodParameter[] parameters = handlerMethod.getMethodParameters();
                    for (int i = 0; i < parameters.length; i++) {
                        if (index == i){
                            fieldName = parameters[i].getParameterName();
                            break;
                        }
                    }
                    return fieldName + defaultMessage;
                }).collect(Collectors.joining(";"));
        log.warn("[constraintViolationExceptionHandler] msg:{}", message);
        return ApiResult.failed(1000,message);
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ApiResult<?> exceptionHandler(Exception e) {
        log.error("[exceptionHandler] msg:{}", e.getMessage());
        return ApiResult.failed(ErrorCode.UNKNOWN.getCode(),e.getMessage());
    }
}
