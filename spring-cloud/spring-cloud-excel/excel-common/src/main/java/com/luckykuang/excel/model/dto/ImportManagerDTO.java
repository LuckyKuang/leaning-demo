package com.luckykuang.excel.model.dto;

import com.luckykuang.excel.enums.ImportBusiness;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author luckykuang
 * @since 2025/2/11 16:13
 */
@Data
public class ImportManagerDTO {
    /**
     * 导入业务
     */
    private ImportBusiness importBusiness;
    /**
     * 导入文件
     */
    private MultipartFile file;
}
