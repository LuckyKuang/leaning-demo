package com.luckykuang.excel.model.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author luckykuang
 * @since 2025/2/11 16:46
 */
@Data
public class StudentDTO {
    @ExcelProperty("名称")
    private String name;
    @ExcelProperty("年龄")
    private Integer age;
}
