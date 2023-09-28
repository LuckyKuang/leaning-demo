/*
 * Copyright 2015-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.luckykuang.serial.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Map;

/**
 * ObjectMapper工具类
 * @author luckykuang
 * @date 2023/9/13 10:22
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ObjectMapperUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper
                .registerModule(new JavaTimeModule())
                // 是否需要排序
                .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)
                // 忽略空bean转json的错误
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                // 取消默认转换timestamps形式
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                // 序列化的时候，过滤null属性
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                // 忽略在json字符串中存在，但在java对象中不存在对应属性的情况，防止错误
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                // 忽略空bean转json的错误
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                // the parameter WRITE_DATES_AS_TIMESTAMPS tells the mapper to represent a Date as a String in JSON
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    /**
     * 对象转json字符串
     *
     * @param obj 对象
     * @param <T> 对象泛型
     * @return json字符串
     */
    public static <T> String obj2Json(T obj) {
        if (obj == null) {
            return null;
        }
        try {
            return obj instanceof String strObj ? strObj : objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("对象解析为json字符串异常", e);
        }
        return null;
    }

    /**
     * Obj转Map
     * @param obj 对象
     * @param <T> 对象泛型
     * @return Map集合
     */
    public static <T> Map obj2Map(T obj) {
        if (obj == null) {
            return null;
        }
        try {
            return objectMapper.readValue(obj2Json(obj),Map.class);
        } catch (Exception e) {
            log.warn("对象解析为Map异常", e);
        }
        return null;
    }

    /**
     * 对象转bytes
     *
     * @param obj 对象
     * @param <T> 对象泛型
     * @return json字符串
     */
    public static <T> byte[] obj2Bytes(T obj) {
        if (obj == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsBytes(obj);
        } catch (Exception e) {
            log.warn("对象解析为byte数组异常", e);
        }
        return null;
    }

    /**
     * 对象转json字符串，并进行格式化
     *
     * @param obj 对象
     * @param <T> 对象泛型
     * @return 格式化后的json字符串
     */
    public static <T> String obj2JsonPretty(T obj) {
        if (obj == null) {
            return null;
        }
        try {
            return obj instanceof String strObj ? strObj : objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("对象解析为json字符串异常", e);
        }
        return null;
    }

    /**
     * json转对象
     * 这里有一个坑，例如List<User> list = stringObj(str,Class<List> data);这里会有问题
     *
     * @param jsonStr json字符串
     * @param clazz   对象
     * @param <T>     对象泛型
     * @return 反序列化后的对象
     */
    public static <T> T str2Obj(String jsonStr, Class<T> clazz) {
        if (StringUtils.isBlank(jsonStr) || null == clazz) {
            return null;
        }
        try {
            return clazz.equals(String.class) ? (T) jsonStr : objectMapper.readValue(jsonStr, clazz);
        } catch (Exception e) {
            log.warn("json字符串解析为对象异常", e);
            return null;
        }
    }

    /**
     * 复杂对象反序列化
     * 使用例子List<User> list = JsonUtil.string2Obj(str, new TypeReference<List<User>>() {});
     *
     * @param str           json对象
     * @param typeReference 引用类型
     * @param <T>           返回值类型
     * @return 反序列化对象
     */
    public static <T> T str2Obj(String str, TypeReference<T> typeReference) {
        if (StringUtils.isBlank(str) || typeReference == null) {
            return null;
        }
        try {
            return (T) (typeReference.getType().equals(String.class) ? str : objectMapper.readValue(str, typeReference));
        } catch (IOException e) {
            log.warn("json字符串解析为对象错误", e);
            return null;
        }
    }

    /**
     * 复杂对象反序列化
     * 使用例子List<User> list = JsonUtil.string2Obj(str, List.class, User.class);
     *
     * @param str             json对象
     * @param collectionClass 定义的class类型
     * @param elementClass    子元素的class类型
     * @param <T>             返回值类型
     * @return 反序列化对象
     */
    public static <T> T str2Obj(String str, Class<?> collectionClass, Class<?>... elementClass) {
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collectionClass, elementClass);
        try {
            return objectMapper.readValue(str, javaType);
        } catch (IOException e) {
            log.warn("json字符串解析为对象错误", e);
            return null;
        }
    }

    /**
     * 对象转换 && 对象深复制
     *
     * @param obj   源对象
     * @param clazz 目标对象引用类型
     * @param <T>   返回值类型
     * @return T
     */
    public static <T> T convert(Object obj, TypeReference<T> clazz) {
        try {
            return objectMapper.convertValue(obj, clazz);
        } catch (Exception e) {
            log.warn("对象转换异常", e);
        }
        return null;
    }

    /**
     * 对象转换 && 对象深复制
     *
     * @param obj   源对象
     * @param clazz 目标对象类型
     * @param <T>   返回值类型
     * @return T
     */
    public static <T> T convert(Object obj, Class<T> clazz) {
        try {
            return objectMapper.convertValue(obj, clazz);
        } catch (Exception e) {
            log.warn("对象转换异常", e);
        }
        return null;
    }

    /**
     * 对象转bytes
     *
     * @param obj 对象
     * @param <T> 对象泛型
     * @return 字节数组
     */
    public static <T> byte[] objToBytes(T obj) {
        if (obj == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsBytes(obj);
        } catch (Exception e) {
            log.warn("对象解析为byte数组异常", e);
        }
        return null;
    }
}
