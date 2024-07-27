package com.luckykuang.redis.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luckykuang.redis.annotation.MarkReqLog;
import com.luckykuang.redis.annotation.MarkRespLog;
import com.luckykuang.redis.base.ApiResult;
import com.luckykuang.redis.model.Student;
import com.luckykuang.redis.util.BusinessExceptionUtils;
import com.luckykuang.redis.util.RedisUtils;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * @author luckykuang
 * @since 2024/7/9 9:37
 */
@Slf4j
@Validated
@RestController
@RequestMapping("test")
@AllArgsConstructor
public class TestController {

    private final RedisUtils redisUtils;
    private final ObjectMapper objectMapper;

    @GetMapping("testValidated")
    public ApiResult<?> testValidated(@NotBlank(message = "自定义消息") String name,
                              @NotBlank String nickname,
                              @NotNull(message = "自定义消息") Integer age){
        return ApiResult.success(name +","+ nickname +","+ age);
    }

    @GetMapping("testValidated2")
    public ApiResult<?> testValidated2(@Validated Student student){
        return ApiResult.success(student);
    }

    @PostMapping("testValidated3")
    public ApiResult<?> testValidated3(@Validated Student student){
        return ApiResult.success(student);
    }

    @MarkReqLog
    @MarkRespLog
    @PostMapping("testRedis")
    public ApiResult<Student> testRedis(@RequestBody @Validated Student student) {
        try {
            String studentJson = objectMapper.writeValueAsString(student);
            boolean hasKey = redisUtils.hasKey("student");
            if (!hasKey){
                log.info("写入redis...");
                redisUtils.set("student", studentJson);
            }
            Object obj = redisUtils.get("student");
            Optional<Object> optional = Optional.ofNullable(obj);
            if (optional.isPresent()) {
                log.info("转换redis值...");
                Student studentObj = objectMapper.readValue(optional.get().toString(),Student.class);
                return ApiResult.success(studentObj);
            }
        } catch (JsonProcessingException e) {
            BusinessExceptionUtils.getErrorMessage(e);
        }
        return ApiResult.success(student);
    }
}
