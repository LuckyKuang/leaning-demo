package com.luckykuang.excel.service;

import com.luckykuang.excel.model.qo.TeacherQO;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author luckykuang
 * @since 2025/2/12 10:02
 */
public interface TeacherService {
    void exportTeacherExcel(TeacherQO teacherQO);

    String importTeacherExcel(MultipartFile file);
}
