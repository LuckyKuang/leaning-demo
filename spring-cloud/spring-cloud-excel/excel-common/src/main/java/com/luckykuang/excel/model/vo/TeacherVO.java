package com.luckykuang.excel.model.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 学生信息导出类
 * @author luckykuang
 * @since 2025/2/11 11:07
 */
@Data
public class TeacherVO {
    @ExcelProperty("序号")
    private Integer reqNum;
    @ExcelProperty("姓名")
    private String name;
    @ExcelProperty("年龄")
    private Integer age;
}
