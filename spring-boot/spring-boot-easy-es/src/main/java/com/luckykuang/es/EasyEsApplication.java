package com.luckykuang.es;

import org.dromara.easyes.spring.annotation.EsMapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author luckykuang
 * @since 2024/8/26 14:04
 */
@SpringBootApplication
@EsMapperScan("com.luckykuang.es.mapper")
public class EasyEsApplication {
    public static void main(String[] args) {
        SpringApplication.run(EasyEsApplication.class, args);
    }
}
