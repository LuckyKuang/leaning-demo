package com.luckykuang.excel.model.entity;

import com.luckykuang.excel.model.base.BaseVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 教师信息
 * @author luckykuang
 * @since 2025/2/11 17:25
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Teacher extends BaseVO {
    private Long id;
    private String name;
    private Integer age;
}
