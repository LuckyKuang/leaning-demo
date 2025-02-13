package com.luckykuang.excel.feign.factroy;

import com.luckykuang.common.exception.BusinessException;
import com.luckykuang.excel.feign.ImportClient;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author luckykuang
 * @since 2025/2/11 15:13
 */
@Component
public class ImportClientFactory implements FallbackFactory<ImportClient> {
    @Override
    public ImportClient create(Throwable cause) {
        return new ImportClient() {
            @Override
            public String importExcel(String businessCode, MultipartFile file) {
                throw new BusinessException("openfeign exception");
            }
        };
    }
}
