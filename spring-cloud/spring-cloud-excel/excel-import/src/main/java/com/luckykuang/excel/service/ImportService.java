package com.luckykuang.excel.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author luckykuang
 * @since 2025/2/11 15:52
 */
public interface ImportService {
    String importExcel(String businessCode, MultipartFile file);
}
