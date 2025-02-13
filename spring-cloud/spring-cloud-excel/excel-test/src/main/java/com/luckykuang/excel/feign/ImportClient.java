package com.luckykuang.excel.feign;

import com.luckykuang.excel.feign.factroy.ImportClientFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author luckykuang
 * @since 2025/2/11 15:12
 */
@FeignClient(
        // 需要调用服务端的服务名
        name = "excel-import",
        // 路径前缀设置
        path = "api/v1/import",
        // 熔断处理
        fallbackFactory = ImportClientFactory.class)
public interface ImportClient {
    @PostMapping(value = "importExcel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String importExcel(@RequestPart("businessCode") String businessCode, @RequestPart("file") MultipartFile file);
}
