package com.luckykuang.excel.model.qo;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author luckykuang
 * @since 2025/2/11 11:08
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StudentQO extends PageQO {
    private String name;
}
