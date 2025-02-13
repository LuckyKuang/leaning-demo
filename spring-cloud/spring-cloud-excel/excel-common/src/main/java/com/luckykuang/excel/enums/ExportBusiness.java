package com.luckykuang.excel.enums;

import com.luckykuang.excel.model.vo.StudentVO;
import com.luckykuang.excel.model.vo.TeacherVO;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 导出业务枚举
 * @author luckykuang
 * @since 2025/2/11 14:22
 */
@Getter
@AllArgsConstructor
public enum ExportBusiness {
    STUDENT_INFO("01","学生信息", StudentVO.class),
    TEACHER_INFO("02","教师信息", TeacherVO.class),
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
     * 导出实体
     */
    private final Class<?> clazz;
}
