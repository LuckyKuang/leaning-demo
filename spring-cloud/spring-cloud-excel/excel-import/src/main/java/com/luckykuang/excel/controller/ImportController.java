package com.luckykuang.excel.controller;

import com.luckykuang.excel.service.ImportService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author luckykuang
 * @since 2025/2/11 15:49
 */
@RestController
@AllArgsConstructor
@RequestMapping("api/v1/import")
public class ImportController {

    private final ImportService importService;

    @PostMapping("importExcel")
    public String importExcel(@RequestPart("businessCode") String businessCode, @RequestPart("file") MultipartFile file) {
        return importService.importExcel(businessCode,file);
    }
}
