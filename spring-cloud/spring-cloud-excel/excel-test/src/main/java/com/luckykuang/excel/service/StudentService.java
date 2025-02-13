package com.luckykuang.excel.service;

import com.luckykuang.excel.model.qo.StudentQO;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author luckykuang
 * @since 2025/2/11 11:05
 */
public interface StudentService {
    void exportStudentExcel(StudentQO studentQO);

    String importStudentExcel(MultipartFile file);
}
