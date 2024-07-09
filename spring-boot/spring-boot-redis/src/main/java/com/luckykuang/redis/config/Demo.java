package com.luckykuang.redis.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.luckykuang.redis.util.BusinessExceptionUtils;

/**
 * @author luckykuang
 * @since 2024/7/8 17:00
 */
public class Demo {
    public static void main(String[] args) {
        try {
            int a = dddZz(44444444444444444L,1);
        } catch (Exception e) {
            BusinessExceptionUtils.getErrorMessage(e);
        }
    }

    public static int dddZ(int a, int b){
        return a/b;
    }

    public static int dddZz(Long a,int b){
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(a,Integer.class);
    }
}
