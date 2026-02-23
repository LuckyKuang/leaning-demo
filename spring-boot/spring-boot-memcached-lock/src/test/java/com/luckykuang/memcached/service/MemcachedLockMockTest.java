package com.luckykuang.memcached.service;

import net.rubyeye.xmemcached.MemcachedClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Memcached分布式锁单元测试（Mock版本）
 * 由于远程Memcached服务器连接问题，使用Mock测试验证代码逻辑
 */
@ExtendWith(MockitoExtension.class)
class MemcachedLockMockTest {

    @Mock
    private MemcachedClient memcachedClient;

    @Mock
    private ScheduledExecutorService scheduledExecutor;

    @Mock
    private ScheduledFuture scheduledFuture;

    private BasicMemcachedLock basicLock;
    private SafeMemcachedLock safeLock;
    private ReentrantMemcachedLock reentrantLock;
    private RenewalMemcachedLock renewalLock;

    private static final String TEST_LOCK_KEY = "test:lock:key";

    @BeforeEach
    void setUp() throws Exception {
        // 创建锁实例
        basicLock = new BasicMemcachedLock(memcachedClient);
        safeLock = new SafeMemcachedLock(memcachedClient);
        reentrantLock = new ReentrantMemcachedLock(memcachedClient);
        renewalLock = new RenewalMemcachedLock(memcachedClient, scheduledExecutor);
    }

    @Test
    void testBasicLock_TryLock_Success() throws Exception {
        when(memcachedClient.add(anyString(), anyInt(), anyString())).thenReturn(true);

        boolean locked = basicLock.tryLock(TEST_LOCK_KEY);
        assertTrue(locked, "应该成功获取锁");

        verify(memcachedClient).add(contains(TEST_LOCK_KEY), eq(30), anyString());
    }

    @Test
    void testBasicLock_TryLock_FailWhenLocked() throws Exception {
        when(memcachedClient.add(anyString(), anyInt(), anyString())).thenReturn(false);

        boolean locked = basicLock.tryLock(TEST_LOCK_KEY);
        assertFalse(locked, "应该获取锁失败");

        verify(memcachedClient).add(contains(TEST_LOCK_KEY), eq(30), anyString());
    }

    @Test
    void testBasicLock_Unlock() throws Exception {
        when(memcachedClient.delete(anyString())).thenReturn(true);

        basicLock.unlock(TEST_LOCK_KEY);

        verify(memcachedClient).delete(contains(TEST_LOCK_KEY));
    }

    @Test
    void testSafeLock_TryLock_Success() throws Exception {
        when(memcachedClient.add(anyString(), anyInt(), anyString())).thenReturn(true);

        boolean locked = safeLock.tryLock(TEST_LOCK_KEY);
        assertTrue(locked, "应该成功获取锁");

        verify(memcachedClient).add(contains(TEST_LOCK_KEY), eq(30), anyString());
    }

    @Test
    void testSafeLock_Unlock_Success() throws Exception {
        String fullKey = "lock:" + TEST_LOCK_KEY;

        // 先获取锁 - 使用Answer来捕获实际存储的值
        final String[] storedValue = new String[1];
        when(memcachedClient.add(eq(fullKey), anyInt(), anyString())).thenAnswer(invocation -> {
            storedValue[0] = invocation.getArgument(2);
            return true;
        });
        safeLock.tryLock(TEST_LOCK_KEY);

        // 验证释放锁 - 返回存储的值
        when(memcachedClient.get(eq(fullKey))).thenReturn(storedValue[0]);
        when(memcachedClient.delete(eq(fullKey))).thenReturn(true);

        boolean unlocked = safeLock.unlock(TEST_LOCK_KEY);
        assertTrue(unlocked, "应该成功释放锁");
        verify(memcachedClient).delete(eq(fullKey));
    }

    @Test
    void testReentrantLock_TryLock_Success() throws Exception {
        when(memcachedClient.add(anyString(), anyInt(), anyString())).thenReturn(true);

        boolean locked = reentrantLock.tryLock(TEST_LOCK_KEY);
        assertTrue(locked, "应该成功获取锁");

        verify(memcachedClient).add(contains(TEST_LOCK_KEY), eq(30), contains("1"));
    }

    @Test
    void testReentrantLock_Reentrant() throws Exception {
        // 第一次获取锁
        when(memcachedClient.add(anyString(), anyInt(), anyString()))
                .thenReturn(true);

        boolean firstLock = reentrantLock.tryLock(TEST_LOCK_KEY);
        assertTrue(firstLock, "第一次应该成功获取锁");

        // 重入 - 模拟已存在的锁值
        when(memcachedClient.get(anyString()))
                .thenReturn("main-1:1"); // 返回当前线程的锁值

        when(memcachedClient.set(anyString(), anyInt(), anyString()))
                .thenReturn(true);

        boolean secondLock = reentrantLock.tryLock(TEST_LOCK_KEY);
        assertTrue(secondLock, "第二次应该成功获取锁（重入）");

        // 验证set被调用，增加计数
        verify(memcachedClient).set(contains(TEST_LOCK_KEY), eq(30), contains("2"));
    }

    @Test
    void testReentrantLock_Unlock() throws Exception {
        when(memcachedClient.get(anyString())).thenReturn("main-1:1");
        when(memcachedClient.delete(anyString())).thenReturn(true);

        boolean unlocked = reentrantLock.unlock(TEST_LOCK_KEY);
        assertTrue(unlocked, "应该成功释放锁");

        verify(memcachedClient).delete(contains(TEST_LOCK_KEY));
    }

    @Test
    void testConcurrentLock_WithMock() throws Exception {
        AtomicInteger addCallCount = new AtomicInteger(0);
        AtomicInteger successCount = new AtomicInteger(0);

        // Mock add方法 - 模拟并发情况
        when(memcachedClient.add(anyString(), anyInt(), anyString())).thenAnswer(invocation -> {
            int count = addCallCount.incrementAndGet();
            // 只有第一次调用返回true，模拟锁已被占用
            return count == 1;
        });

        when(memcachedClient.delete(anyString())).thenReturn(true);

        int threadCount = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    if (basicLock.tryLock(TEST_LOCK_KEY)) {
                        successCount.incrementAndGet();
                        try {
                            Thread.sleep(50);
                        } finally {
                            basicLock.unlock(TEST_LOCK_KEY);
                        }
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

        // 验证至少有一次成功获取锁
        assertTrue(successCount.get() > 0, "至少应该有一次成功获取锁");
        // 验证有多次尝试获取锁
        assertTrue(addCallCount.get() > 1, "应该有多次尝试获取锁");
    }

    @Test
    void testLockExpireTime() throws Exception {
        when(memcachedClient.add(anyString(), eq(2), anyString())).thenReturn(true);

        boolean locked = basicLock.tryLock(TEST_LOCK_KEY, 2);
        assertTrue(locked, "应该成功获取锁");

        verify(memcachedClient).add(contains(TEST_LOCK_KEY), eq(2), anyString());
    }

    // ==================== 自动续期锁测试 ====================

    @Test
    void testRenewalLock_TryLock_Success() throws Exception {
        // Mock scheduledExecutor
        when(scheduledExecutor.scheduleAtFixedRate(any(Runnable.class), anyLong(), anyLong(), any(TimeUnit.class)))
                .thenReturn(scheduledFuture);
        when(memcachedClient.add(anyString(), anyInt(), anyString())).thenReturn(true);

        boolean locked = renewalLock.tryLock(TEST_LOCK_KEY);
        assertTrue(locked, "应该成功获取锁");

        verify(memcachedClient).add(contains(TEST_LOCK_KEY), eq(30), anyString());
        verify(scheduledExecutor).scheduleAtFixedRate(any(Runnable.class), anyLong(), anyLong(), any(TimeUnit.class));
    }

    @Test
    void testRenewalLock_TryLock_WithCustomExpire() throws Exception {
        // Mock scheduledExecutor
        when(scheduledExecutor.scheduleAtFixedRate(any(Runnable.class), anyLong(), anyLong(), any(TimeUnit.class)))
                .thenReturn(scheduledFuture);
        when(memcachedClient.add(anyString(), eq(10), anyString())).thenReturn(true);

        boolean locked = renewalLock.tryLock(TEST_LOCK_KEY, 10);
        assertTrue(locked, "应该成功获取锁");

        verify(memcachedClient).add(contains(TEST_LOCK_KEY), eq(10), anyString());
    }

    @Test
    void testRenewalLock_TryLock_FailWhenLocked() throws Exception {
        when(memcachedClient.add(anyString(), anyInt(), anyString())).thenReturn(false);

        boolean locked = renewalLock.tryLock(TEST_LOCK_KEY);
        assertFalse(locked, "应该获取锁失败");

        verify(memcachedClient).add(contains(TEST_LOCK_KEY), eq(30), anyString());
        // 不应该启动续期任务
        verify(scheduledExecutor, never()).scheduleAtFixedRate(any(Runnable.class), anyLong(), anyLong(), any(TimeUnit.class));
    }

    @Test
    void testRenewalLock_Unlock_Success() throws Exception {
        // Mock scheduledExecutor
        when(scheduledExecutor.scheduleAtFixedRate(any(Runnable.class), anyLong(), anyLong(), any(TimeUnit.class)))
                .thenReturn(scheduledFuture);
        when(scheduledFuture.isCancelled()).thenReturn(false);

        String fullKey = "lock:" + TEST_LOCK_KEY;

        // Mock获取锁 - 使用Answer捕获实际存储的值
        final String[] storedValue = new String[1];
        when(memcachedClient.add(eq(fullKey), anyInt(), anyString())).thenAnswer(invocation -> {
            storedValue[0] = invocation.getArgument(2);
            return true;
        });
        renewalLock.tryLock(TEST_LOCK_KEY);

        // Mock释放锁 - 返回存储的值
        when(memcachedClient.get(eq(fullKey))).thenReturn(storedValue[0]);
        when(memcachedClient.delete(eq(fullKey))).thenReturn(true);

        boolean unlocked = renewalLock.unlock(TEST_LOCK_KEY);
        assertTrue(unlocked, "应该成功释放锁");

        verify(memcachedClient).delete(eq(fullKey));
    }

    @Test
    void testRenewalLock_Unlock_FailWhenValueMismatch() throws Exception {
        // Mock scheduledExecutor
        when(scheduledExecutor.scheduleAtFixedRate(any(Runnable.class), anyLong(), anyLong(), any(TimeUnit.class)))
                .thenReturn(scheduledFuture);
        when(scheduledFuture.isCancelled()).thenReturn(false);

        String fullKey = "lock:" + TEST_LOCK_KEY;

        // Mock获取锁
        when(memcachedClient.add(eq(fullKey), anyInt(), anyString())).thenReturn(true);
        renewalLock.tryLock(TEST_LOCK_KEY);

        // Mock值不匹配
        when(memcachedClient.get(eq(fullKey))).thenReturn("different-value");

        boolean unlocked = renewalLock.unlock(TEST_LOCK_KEY);
        assertFalse(unlocked, "值不匹配时释放锁应该失败");

        verify(memcachedClient, never()).delete(eq(fullKey));
    }

    @Test
    void testRenewalLock_Destroy() throws Exception {
        renewalLock.destroy();

        // 验证destroy方法正常执行（无异常抛出）
        assertTrue(true, "destroy方法应该正常执行");
    }
}
