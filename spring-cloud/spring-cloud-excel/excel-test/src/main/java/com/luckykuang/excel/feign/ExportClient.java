package com.luckykuang.excel.feign;

import com.luckykuang.excel.feign.factroy.ExportClientFactory;
import com.luckykuang.excel.model.dto.ExportManagerDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author luckykuang
 * @since 2025/2/11 15:12
 */
@FeignClient(
        // 需要调用服务端的服务名
        name = "excel-export",
        // 路径前缀设置
        path = "api/v1/export",
        // 熔断处理
        fallbackFactory = ExportClientFactory.class)
public interface ExportClient {
    @PostMapping(value = "exportExcel", consumes = MediaType.APPLICATION_JSON_VALUE)
    void exportExcel(@RequestBody ExportManagerDTO<?> exportManagerDTO);
}
