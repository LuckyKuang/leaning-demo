package com.luckykuang.excel.controller;

import com.luckykuang.excel.model.dto.ExportManagerDTO;
import com.luckykuang.excel.service.ExportService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author luckykuang
 * @since 2025/2/10 17:46
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("api/v1/export")
public class ExportController {

    private final ExportService exportService;

    @PostMapping("exportExcel")
    public void exportExcel(@RequestBody ExportManagerDTO<?> exportManagerDTO) {
        exportService.exportExcel(exportManagerDTO);
    }
}
