package com.luckykuang.excel.feign.factroy;

import com.luckykuang.common.exception.BusinessException;
import com.luckykuang.excel.feign.ExportClient;
import com.luckykuang.excel.model.dto.ExportManagerDTO;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @author luckykuang
 * @since 2025/2/11 15:13
 */
@Component
public class ExportClientFactory implements FallbackFactory<ExportClient> {
    @Override
    public ExportClient create(Throwable cause) {
        return new ExportClient() {
            @Override
            public void exportExcel(ExportManagerDTO<?> exportManagerDTO) {
                throw new BusinessException("openfeign exception");
            }
        };
    }
}
