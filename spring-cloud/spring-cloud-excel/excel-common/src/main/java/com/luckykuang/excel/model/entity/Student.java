package com.luckykuang.excel.model.entity;

import com.luckykuang.excel.model.base.BaseVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 学生信息
 * @author luckykuang
 * @since 2025/2/11 11:02
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Student extends BaseVO {
    private Long id;
    private String name;
    private Integer age;
}
