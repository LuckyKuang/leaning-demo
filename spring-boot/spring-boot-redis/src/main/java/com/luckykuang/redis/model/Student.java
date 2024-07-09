package com.luckykuang.redis.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author luckykuang
 * @since 2024/7/9 11:22
 */
@Data
public class Student implements Serializable {
    @Serial
    private static final long serialVersionUID = -2442029153641384479L;

    @NotNull
    private Integer id;
    @NotBlank
    private String name;
    @Max(99)
    @Min(0)
    @NotNull(message = "自定义消息")
    private Integer age;
    @NotEmpty
    private List<String> list;
}
