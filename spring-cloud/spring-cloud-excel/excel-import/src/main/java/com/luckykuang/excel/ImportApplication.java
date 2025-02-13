package com.luckykuang.excel;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author luckykuang
 * @since 2025/2/12 10:05
 */
@EnableDiscoveryClient
@MapperScan("com.luckykuang.**.mapper")
@SpringBootApplication
public class ImportApplication {
    public static void main(String[] args) {
        SpringApplication.run(ImportApplication.class, args);
    }
}
