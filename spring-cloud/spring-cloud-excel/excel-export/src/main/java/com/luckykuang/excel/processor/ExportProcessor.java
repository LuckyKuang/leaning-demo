package com.luckykuang.excel.processor;

import com.luckykuang.excel.model.dto.ExportManagerDTO;

import java.util.List;

/**
 * @author luckykuang
 * @since 2025/2/11 15:42
 */
public interface ExportProcessor {
    List<?> process(ExportManagerDTO<?> exportManagerDTO);
}
