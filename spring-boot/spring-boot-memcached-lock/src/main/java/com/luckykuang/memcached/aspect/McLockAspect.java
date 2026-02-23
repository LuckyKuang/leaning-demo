package com.luckykuang.memcached.aspect;

import com.luckykuang.memcached.annotation.McLock;
import com.luckykuang.memcached.service.ReentrantMemcachedLock;
import com.luckykuang.memcached.service.RenewalMemcachedLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

/**
 * Memcached分布式锁AOP切面
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class McLockAspect {

    private final ReentrantMemcachedLock reentrantLock;
    private final RenewalMemcachedLock renewalLock;

    private final ExpressionParser parser = new SpelExpressionParser();
    private final ParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();

    @Around("@annotation(mcLock)")
    public Object around(ProceedingJoinPoint joinPoint, McLock mcLock) throws Throwable {
        // 1. 解析锁的key
        String lockKey = parseLockKey(joinPoint, mcLock);

        // 2. 选择合适的锁实现
        Object lockService = mcLock.autoRenewal() ? renewalLock : reentrantLock;

        // 3. 尝试获取锁
        boolean locked = false;
        try {
            locked = tryLock(lockService, lockKey, mcLock);

            if (!locked) {
                return handleLockFailure(mcLock, lockKey);
            }

            log.info("获取分布式锁成功: {}", lockKey);

            // 4. 执行业务逻辑
            return joinPoint.proceed();

        } finally {
            // 5. 释放锁
            if (locked) {
                unlock(lockService, lockKey);
                log.info("释放分布式锁: {}", lockKey);
            }
        }
    }

    /**
     * 尝试获取锁
     */
    private boolean tryLock(Object lockService, String lockKey, McLock mcLock) {
        long expireTime = mcLock.timeUnit().toSeconds(mcLock.expireTime());

        if (lockService instanceof ReentrantMemcachedLock) {
            return ((ReentrantMemcachedLock) lockService).tryLock(lockKey, (int) expireTime);
        } else if (lockService instanceof RenewalMemcachedLock) {
            return ((RenewalMemcachedLock) lockService).tryLock(lockKey, (int) expireTime);
        }

        return false;
    }

    /**
     * 释放锁
     */
    private void unlock(Object lockService, String lockKey) {
        if (lockService instanceof ReentrantMemcachedLock) {
            ((ReentrantMemcachedLock) lockService).unlock(lockKey);
        } else if (lockService instanceof RenewalMemcachedLock) {
            ((RenewalMemcachedLock) lockService).unlock(lockKey);
        }
    }

    /**
     * 解析锁的key（支持SpEL表达式）
     */
    private String parseLockKey(ProceedingJoinPoint joinPoint, McLock mcLock) {
        String keyExpression = mcLock.key();
        String prefix = mcLock.prefix();

        // 如果不包含SpEL表达式，直接返回
        if (!keyExpression.contains("#")) {
            return prefix + keyExpression;
        }

        // 解析SpEL表达式
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = nameDiscoverer.getParameterNames(signature.getMethod());
        Object[] args = joinPoint.getArgs();

        EvaluationContext context = new StandardEvaluationContext();
        if (parameterNames != null) {
            for (int i = 0; i < parameterNames.length; i++) {
                context.setVariable(parameterNames[i], args[i]);
            }
        }

        Expression expression = parser.parseExpression(keyExpression);
        String value = expression.getValue(context, String.class);

        return prefix + value;
    }

    /**
     * 处理获取锁失败的情况
     */
    private Object handleLockFailure(McLock mcLock, String lockKey) {
        log.warn("获取分布式锁失败: {}", lockKey);

        return switch (mcLock.failStrategy()) {
            case THROW_EXCEPTION ->
                throw new RuntimeException("获取分布式锁失败: " + lockKey);
            case FAIL_FAST -> {
                yield null;
            }
            case IGNORE -> {
                log.warn("忽略锁获取失败，继续执行");
                yield null;
            }
        };
    }
}
