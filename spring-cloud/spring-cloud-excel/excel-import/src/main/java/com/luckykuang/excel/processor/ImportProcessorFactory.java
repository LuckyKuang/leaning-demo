package com.luckykuang.excel.processor;

import com.luckykuang.common.exception.BusinessException;
import com.luckykuang.excel.aspect.ExcelImport;
import com.luckykuang.excel.enums.ImportBusiness;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 导入工厂方法
 * @author luckykuang
 * @since 2025/2/11 17:09
 */
@Component
public class ImportProcessorFactory implements BeanPostProcessor {

    private static final Map<ImportBusiness, ImportProcessor> IMPORT_STRATEGY_MAP = new ConcurrentHashMap<>();

    public ImportProcessor getImportProcessor(ImportBusiness importBusiness) {
        if (!IMPORT_STRATEGY_MAP.containsKey(importBusiness)) {
            throw new BusinessException("The imported type does not exist.");
        }
        return IMPORT_STRATEGY_MAP.get(importBusiness);
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 只对实现了 ImportProcessor 的类做操作
        if (bean instanceof ImportProcessor importProcessor) {
            // 获取对象运行时该对象的类
            Class<?> clazz = AopUtils.getTargetClass(bean);
            // 获取自定义的注解
            ExcelImport annotation = clazz.getAnnotation(ExcelImport.class);
            // 绑定对应关系
            IMPORT_STRATEGY_MAP.put(annotation.value(), importProcessor);
        }
        return bean;
    }
}
