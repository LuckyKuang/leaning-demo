package com.luckykuang.redis;

import com.luckykuang.redis.util.RedisUtils;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author luckykuang
 * @since 2024/9/20 14:01
 */
@SpringBootTest
class RedisApplicationTest {

    @Resource
    private RedisUtils redisUtils;

    @Test
    void test() {
        redisUtils.set("hello", "world");
        assertEquals("world", redisUtils.get("hello"));
    }

    @Test
    void test1() {
        redisUtils.set("a:b:a", "world");
        redisUtils.set("a:b:b", "world");
        redisUtils.set("a:b:c", "world");
        redisUtils.set("a:b:d", "world");
    }

    @Test
    void test2() {
        Object o = redisUtils.get("a:b:c");
        System.out.println(o);
    }

    @Test
    void test3() {
        Set<String> keys = redisUtils.keys("a:b:*");
        System.out.println(keys);
        keys.forEach(key -> {
            redisUtils.del(key);
        });
    }
}