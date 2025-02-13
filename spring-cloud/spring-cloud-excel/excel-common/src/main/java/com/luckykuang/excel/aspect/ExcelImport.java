package com.luckykuang.excel.aspect;

import com.luckykuang.excel.enums.ImportBusiness;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author luckykuang
 * @since 2025/2/11 17:35
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ExcelImport {
    ImportBusiness value();
}
