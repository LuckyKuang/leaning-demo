package com.luckykuang.redis.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis配置
 * @author luckykuang
 * @since 2024/6/18 10:20
 */
@Configuration
@AllArgsConstructor
public class RedisConfig {

    private final ObjectMapper objectMapper;
    private final RedisProperties redisProperties;

    /**
     * Lettuce连接池配置
     * @return LettuceConnectionFactory
     */
    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory(){
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setDatabase(redisProperties.getDatabase());
        redisStandaloneConfiguration.setHostName(redisProperties.getHost());
        redisStandaloneConfiguration.setPort(redisProperties.getPort());
        redisStandaloneConfiguration.setPassword(redisProperties.getPassword());
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisStandaloneConfiguration);
        // true: 则共享连接将用于常规操作，LettuceConnectionProvider 将用于选择仅用于阻塞和tx操作的连接，这些操作不应该共享连接。
        // false: 则所有操作将使用新的(或池化的)连接。
        lettuceConnectionFactory.setValidateConnection(false);
        return lettuceConnectionFactory;
    }

    /**
     * 全局配置Redis序列化方式
     * @return RedisTemplate<String, Object>
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory lettuceConnectionFactory) {
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(objectMapper,Object.class);
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        // 设置此模板使用字符串值序列化器
        redisTemplate.setStringSerializer(new StringRedisSerializer());
        // 设置默认键值对序列化方式
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(serializer);
        // 设置散列键值对序列化方式
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(serializer);
        // 设置Redis连接工厂
        redisTemplate.setConnectionFactory(lettuceConnectionFactory);
        return redisTemplate;
    }
}
