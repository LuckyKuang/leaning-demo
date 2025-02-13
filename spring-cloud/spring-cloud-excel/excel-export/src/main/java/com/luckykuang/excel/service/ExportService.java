package com.luckykuang.excel.service;

import com.luckykuang.excel.model.dto.ExportManagerDTO;

/**
 * @author luckykuang
 * @since 2025/2/10 17:51
 */
public interface ExportService {
    void exportExcel(ExportManagerDTO<?> exportManagerDTO);
}
