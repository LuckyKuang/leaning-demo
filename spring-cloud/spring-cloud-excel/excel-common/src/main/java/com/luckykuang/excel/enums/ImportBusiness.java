package com.luckykuang.excel.enums;

import com.luckykuang.common.exception.BusinessException;
import com.luckykuang.excel.model.dto.StudentDTO;
import com.luckykuang.excel.model.dto.TeacherDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author luckykuang
 * @since 2025/2/11 16:11
 */
@Getter
@AllArgsConstructor
public enum ImportBusiness {
    STUDENT_INFO("01","学生信息", StudentDTO.class),
    TEACHER_INFO("02","教师信息", TeacherDTO.class),
    ;
    /**
     * 业务编码
     */
    private final String code;
    /**
     * 业务名称
     */
    private final String name;
    /**
     * 导入实体
     */
    private final Class<?> clazz;

    public static ImportBusiness getByCode(String code) {
        for (ImportBusiness importBusiness : ImportBusiness.values()) {
            if (importBusiness.code.equals(code)) {
                return importBusiness;
            }
        }
        throw new BusinessException("ImportBusiness code error");
    }
}
