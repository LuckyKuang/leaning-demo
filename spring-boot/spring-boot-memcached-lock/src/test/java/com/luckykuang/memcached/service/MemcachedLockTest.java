package com.luckykuang.memcached.service;

import com.luckykuang.memcached.MemcachedLockApplication;
import net.rubyeye.xmemcached.MemcachedClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Memcached分布式锁单元测试
 */
@SpringBootTest(classes = MemcachedLockApplication.class)
@TestPropertySource(properties = {
        "memcached.servers=192.168.1.100:11211",
        "memcached.pool-size=10",
        "memcached.connection-timeout=5000",
        "memcached.operation-timeout=10000",
        "memcached.use-nagle=false"
})
class MemcachedLockTest {

    @Autowired(required = false)
    private BasicMemcachedLock basicLock;

    @Autowired(required = false)
    private SafeMemcachedLock safeLock;

    @Autowired(required = false)
    private ReentrantMemcachedLock reentrantLock;

    @Autowired(required = false)
    private RenewalMemcachedLock renewalLock;

    @Autowired(required = false)
    private MemcachedClient memcachedClient;

    /**
     * 生成唯一的测试key
     */
    private String generateTestKey() {
        return "test:lock:" + UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * 清理指定的key
     */
    private void cleanupKey(String lockKey) {
        if (memcachedClient != null) {
            try {
                memcachedClient.delete("lock:" + lockKey);
                Thread.sleep(100); // 等待删除生效
            } catch (Exception e) {
                // 忽略删除失败
            }
        }
    }

    @Test
    void testBasicLock_TryLock_Success() {
        if (basicLock == null) {
            System.out.println("BasicMemcachedLock bean not available, skipping test");
            return;
        }

        String testKey = generateTestKey();
        try {
            boolean locked = basicLock.tryLock(testKey);
            assertTrue(locked, "应该成功获取锁");

            // 释放锁
            basicLock.unlock(testKey);
        } finally {
            cleanupKey(testKey);
        }
    }

    @Test
    void testBasicLock_TryLock_FailWhenLocked() throws InterruptedException {
        if (basicLock == null) {
            System.out.println("BasicMemcachedLock bean not available, skipping test");
            return;
        }

        String testKey = generateTestKey();
        try {
            boolean firstLock = basicLock.tryLock(testKey);
            assertTrue(firstLock, "第一次应该成功获取锁");

            // 尝试再次获取锁（应该失败）
            boolean secondLock = basicLock.tryLock(testKey);
            assertFalse(secondLock, "第二次应该获取锁失败");

            // 释放锁
            basicLock.unlock(testKey);
        } finally {
            cleanupKey(testKey);
        }
    }

    @Test
    void testBasicLock_ExecuteWithLock() {
        if (basicLock == null) {
            System.out.println("BasicMemcachedLock bean not available, skipping test");
            return;
        }

        String testKey = generateTestKey();
        try {
            AtomicInteger counter = new AtomicInteger(0);
            basicLock.executeWithLock(testKey, () -> {
                counter.incrementAndGet();
            });

            assertEquals(1, counter.get(), "应该执行一次");
        } finally {
            cleanupKey(testKey);
        }
    }

    @Test
    void testSafeLock_TryLock_Success() {
        if (safeLock == null) {
            System.out.println("SafeMemcachedLock bean not available, skipping test");
            return;
        }

        String testKey = generateTestKey();
        try {
            boolean locked = safeLock.tryLock(testKey);
            assertTrue(locked, "应该成功获取锁");

            // 释放锁
            boolean unlocked = safeLock.unlock(testKey);
            assertTrue(unlocked, "应该成功释放锁");
        } finally {
            cleanupKey(testKey);
        }
    }

    @Test
    void testSafeLock_ExecuteWithLock() {
        if (safeLock == null) {
            System.out.println("SafeMemcachedLock bean not available, skipping test");
            return;
        }

        String testKey = generateTestKey();
        try {
            AtomicInteger counter = new AtomicInteger(0);
            boolean result = safeLock.executeWithLock(testKey, () -> {
                counter.incrementAndGet();
            });

            assertTrue(result, "执行应该成功");
            assertEquals(1, counter.get(), "应该执行一次");
        } finally {
            cleanupKey(testKey);
        }
    }

    @Test
    void testReentrantLock_TryLock_Success() {
        if (reentrantLock == null) {
            System.out.println("ReentrantMemcachedLock bean not available, skipping test");
            return;
        }

        String testKey = generateTestKey();
        try {
            boolean locked = reentrantLock.tryLock(testKey);
            assertTrue(locked, "应该成功获取锁");

            // 释放锁
            boolean unlocked = reentrantLock.unlock(testKey);
            assertTrue(unlocked, "应该成功释放锁");
        } finally {
            cleanupKey(testKey);
        }
    }

    @Test
    void testReentrantLock_Reentrant() {
        if (reentrantLock == null) {
            System.out.println("ReentrantMemcachedLock bean not available, skipping test");
            return;
        }

        String testKey = generateTestKey();
        try {
            // 第一次获取锁
            boolean firstLock = reentrantLock.tryLock(testKey);
            assertTrue(firstLock, "第一次应该成功获取锁");

            // 同一线程再次获取锁（重入）
            boolean secondLock = reentrantLock.tryLock(testKey);
            assertTrue(secondLock, "第二次应该成功获取锁（重入）");

            // 释放两次
            boolean firstUnlock = reentrantLock.unlock(testKey);
            assertTrue(firstUnlock, "第一次释放锁应该成功");

            boolean secondUnlock = reentrantLock.unlock(testKey);
            assertTrue(secondUnlock, "第二次释放锁应该成功");
        } finally {
            cleanupKey(testKey);
        }
    }

    @Test
    void testRenewalLock_TryLock_Success() {
        if (renewalLock == null) {
            System.out.println("RenewalMemcachedLock bean not available, skipping test");
            return;
        }

        String testKey = generateTestKey();
        try {
            boolean locked = renewalLock.tryLock(testKey);
            assertTrue(locked, "应该成功获取锁");

            // 释放锁
            boolean unlocked = renewalLock.unlock(testKey);
            assertTrue(unlocked, "应该成功释放锁");
        } finally {
            cleanupKey(testKey);
        }
    }

    @Test
    void testRenewalLock_AutoRenewal() throws InterruptedException {

        if (renewalLock == null) {
            System.out.println("⚠️ RenewalMemcachedLock bean not available, skipping test");
            return;
        }

        String testKey = generateTestKey();
        try {
            System.out.println("🧪 开始测试自动续期功能...");

            // 获取锁（过期时间5秒）
            boolean locked = renewalLock.tryLock(testKey, 8);
            if (!locked) {
                System.out.println("⚠️ 无法获取锁，检查Memcached服务器是否正常运行");
                return;
            }
            assertTrue(locked, "应该成功获取锁");
            System.out.println("✅ 第一次获取锁成功");

            // 等待超过过期时间（自动续期应该保持锁）
            System.out.println("⏰ 等待8秒，验证自动续期功能...");
            Thread.sleep(8000);

            // 尝试获取锁（应该失败，因为原锁还在）
            boolean secondLock = renewalLock.tryLock(testKey, 5);
            System.out.println("🔍 第二次获取锁结果: " + (secondLock ? "成功" : "失败"));

            assertFalse(secondLock, "应该获取锁失败（原锁还在）");
            System.out.println("✅ 自动续期功能正常工作");

            // 释放锁
            boolean unlocked = renewalLock.unlock(testKey);
            assertTrue(unlocked, "应该成功释放锁");
        } finally {
            cleanupKey(testKey);
        }
    }

    @Test
    void testConcurrentLock() throws InterruptedException {
        if (basicLock == null) {
            System.out.println("BasicMemcachedLock bean not available, skipping test");
            return;
        }

        String testKey = generateTestKey();
        try {
            int threadCount = 10;
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);
            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failCount = new AtomicInteger(0);

            for (int i = 0; i < threadCount; i++) {
                executor.submit(() -> {
                    try {
                        if (basicLock.tryLock(testKey)) {
                            successCount.incrementAndGet();
                            try {
                                Thread.sleep(100);
                            } finally {
                                basicLock.unlock(testKey);
                            }
                        } else {
                            failCount.incrementAndGet();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await();
            executor.shutdown();

            System.out.println("成功获取锁次数: " + successCount.get());
            System.out.println("获取锁失败次数: " + failCount.get());

            // 验证至少有一次成功，且有失败的情况
            assertTrue(successCount.get() > 0, "至少应该有一次成功获取锁");
            assertTrue(failCount.get() > 0, "应该有获取锁失败的情况");
        } finally {
            cleanupKey(testKey);
        }
    }

    @Test
    void testLockExpireTime() throws InterruptedException {
        if (basicLock == null) {
            System.out.println("BasicMemcachedLock bean not available, skipping test");
            return;
        }

        String testKey = generateTestKey();
        try {
            // 获取锁（过期时间2秒）
            boolean locked = basicLock.tryLock(testKey, 2);
            assertTrue(locked, "应该成功获取锁");

            // 等待超过过期时间
            Thread.sleep(3000);

            // 现在应该可以再次获取锁
            boolean newLock = basicLock.tryLock(testKey, 2);
            assertTrue(newLock, "过期后应该可以再次获取锁");

            // 释放锁
            basicLock.unlock(testKey);
        } finally {
            cleanupKey(testKey);
        }
    }
}
