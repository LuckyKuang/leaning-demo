package com.luckykuang.excel.controller;

import com.luckykuang.excel.model.qo.StudentQO;
import com.luckykuang.excel.service.StudentService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author luckykuang
 * @since 2025/2/11 15:16
 */
@RestController
@AllArgsConstructor
@RequestMapping("api/v1/student")
public class StudentController {

    private final StudentService studentService;

    @PostMapping("exportStudentExcel")
    public String exportStudentExcel(@RequestBody StudentQO studentQO) {
        studentService.exportStudentExcel(studentQO);
        return "SUCCESS";
    }

    @PostMapping("importStudentExcel")
    public String importStudentExcel(@RequestPart("file") MultipartFile file) {
        return studentService.importStudentExcel(file);
    }
}
