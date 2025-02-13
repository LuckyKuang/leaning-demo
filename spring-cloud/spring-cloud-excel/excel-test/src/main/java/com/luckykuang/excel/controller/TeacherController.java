package com.luckykuang.excel.controller;

import com.luckykuang.excel.model.qo.TeacherQO;
import com.luckykuang.excel.service.TeacherService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author luckykuang
 * @since 2025/2/11 15:16
 */
@RestController
@AllArgsConstructor
@RequestMapping("api/v1/teacher")
public class TeacherController {

    private final TeacherService teacherService;

    @PostMapping("exportTeacherExcel")
    public String exportTeacherExcel(@RequestBody TeacherQO teacherQO) {
        teacherService.exportTeacherExcel(teacherQO);
        return "SUCCESS";
    }

    @PostMapping("importTeacherExcel")
    public String importTeacherExcel(@RequestPart("file") MultipartFile file) {
        return teacherService.importTeacherExcel(file);
    }
}
