package com.luckykuang.redis.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.luckykuang.redis.util.DateUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Jackson序列化配置
 * @author luckykuang
 * @since 2024/6/18 12:05
 */
@Configuration
public class ObjectMapperConfig {

    /**
     * 全局Jackson序列化配置
     * @return ObjectMapper
     */
    @Bean
    public ObjectMapper objectMapper(){
        ObjectMapper objectMapper = new ObjectMapper();
        // 是否需要排序
        objectMapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
        // 忽略空bean转json的错误
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // 取消默认转换timestamps形式
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        // 反序列化的时候如果多了其他属性,不抛出异常
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 序列化时忽略null值
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // 忽略在json字符串中存在，但在java对象中不存在对应属性的情况，防止错误
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        // 忽略空bean转json的错误
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        // 忽略jackson2无法反序列化LocalDateTime的问题，不将日期转换为时间戳
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 这将使所有成员字段都可以序列化，而不需要进一步的注释，而不仅仅是公共字段(默认设置)。
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 配置自定义格式的序列化
        objectMapper.registerModule(getJavaTimeModule());
        // 激活多态类型验证器 (默认类型 LaissezFaireSubTypeValidator.instance) 【慎用】
//        objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(),ObjectMapper.DefaultTyping.NON_FINAL);
        return objectMapper;
    }

    /**
     * 配置自定义格式的序列化
     * @return JavaTimeModule
     */
    private static JavaTimeModule getJavaTimeModule() {
        JavaTimeModule javaTimeModule = new JavaTimeModule();

        // Long/BigInteger/BigDecimal类型转String类型，解决精度丢失问题，特别是前端js精度丢失问题
        javaTimeModule.addSerializer(Long.class, ToStringSerializer.instance);
        javaTimeModule.addSerializer(Long.TYPE,ToStringSerializer.instance);
        javaTimeModule.addSerializer(BigInteger.class, ToStringSerializer.instance);
        javaTimeModule.addSerializer(BigDecimal.class, ToStringSerializer.instance);

        // String转LocalDateTime/LocalDate/LocalTime
        // 序列化
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DateUtils.YYYY_MM_DD_HH_MM_SS)));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(DateUtils.YYYY_MM_DD)));
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern(DateUtils.HH_MM_SS)));
        // 反序列，只能反序列化Body里的json格式数据
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DateUtils.YYYY_MM_DD_HH_MM_SS)));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(DateUtils.YYYY_MM_DD)));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern(DateUtils.HH_MM_SS)));

        return javaTimeModule;
    }
}
