package com.luckykuang.excel.processor;

import com.luckykuang.common.exception.BusinessException;
import com.luckykuang.excel.aspect.ExportExcel;
import com.luckykuang.excel.enums.ExportBusiness;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author luckykuang
 * @since 2025/2/11 17:47
 */
@Component
public class ExportProcessorFactory implements BeanPostProcessor {
    private static final Map<ExportBusiness, ExportProcessor> EXPORT_STRATEGY_MAP = new ConcurrentHashMap<>();

    public ExportProcessor getExportProcessor(ExportBusiness exportBusiness) {
        if (!EXPORT_STRATEGY_MAP.containsKey(exportBusiness)) {
            throw new BusinessException("The exported type does not exist.");
        }
        return EXPORT_STRATEGY_MAP.get(exportBusiness);
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 只对实现了 ExportProcessor 的类做操作
        if (bean instanceof ExportProcessor exportProcessor) {
            // 获取对象运行时该对象的类
            Class<?> clazz = AopUtils.getTargetClass(bean);
            // 获取自定义的注解
            ExportExcel annotation = clazz.getAnnotation(ExportExcel.class);
            // 绑定对应关系
            EXPORT_STRATEGY_MAP.put(annotation.value(), exportProcessor);
        }
        return bean;
    }
}
