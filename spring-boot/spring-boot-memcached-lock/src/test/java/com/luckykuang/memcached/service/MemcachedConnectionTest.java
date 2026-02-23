package com.luckykuang.memcached.service;

import com.luckykuang.memcached.MemcachedLockApplication;
import net.rubyeye.xmemcached.MemcachedClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Memcached连接测试
 */
@SpringBootTest(classes = MemcachedLockApplication.class)
@TestPropertySource(properties = {
        "memcached.servers=192.168.1.100:11211",
        "memcached.pool-size=10",
        "memcached.connection-timeout=5000",
        "memcached.operation-timeout=10000",
        "memcached.use-nagle=false"
})
class MemcachedConnectionTest {

    @Autowired(required = false)
    private MemcachedClient memcachedClient;

    @Test
    void testMemcachedConnection() throws Exception {
        assertNotNull(memcachedClient, "MemcachedClient should be autowired");

        // 测试基本操作
        String testKey = "test:connection:" + System.currentTimeMillis();
        String testValue = "test-value";

        // Set操作
        boolean set = memcachedClient.set(testKey, 60, testValue);
        System.out.println("Set操作结果: " + set);
        assertTrue(set, "Set操作应该成功");

        // Get操作
        String value = memcachedClient.get(testKey);
        System.out.println("Get操作结果: " + value);
        assertEquals(testValue, value, "Get应该返回设置的值");

        // Add操作
        String addKey = "test:add:" + System.currentTimeMillis();
        boolean add = memcachedClient.add(addKey, 60, "add-value");
        System.out.println("Add操作结果: " + add);
        assertTrue(add, "Add操作应该成功");

        // 重复Add操作（应该失败）
        boolean addAgain = memcachedClient.add(addKey, 60, "add-value-2");
        System.out.println("重复Add操作结果: " + addAgain);
        assertFalse(addAgain, "重复Add操作应该失败");

        // Delete操作
        boolean deleted = memcachedClient.delete(addKey);
        System.out.println("Delete操作结果: " + deleted);
        assertTrue(deleted, "Delete操作应该成功");

        // 清理
        memcachedClient.delete(testKey);
    }
}
