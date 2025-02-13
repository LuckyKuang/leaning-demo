package com.luckykuang.excel.processor;

import com.luckykuang.excel.model.dto.ImportManagerDTO;


/**
 * @author luckykuang
 * @since 2025/2/11 16:23
 */
public interface ImportProcessor {
    void process(ImportManagerDTO importManagerDTO);
}
