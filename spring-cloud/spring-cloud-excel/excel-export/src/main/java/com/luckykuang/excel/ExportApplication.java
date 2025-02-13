package com.luckykuang.excel;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author luckykuang
 * @since 2025/2/10 17:47
 */
@EnableDiscoveryClient
@MapperScan("com.luckykuang.**.mapper")
@SpringBootApplication
public class ExportApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExportApplication.class, args);
    }
}
