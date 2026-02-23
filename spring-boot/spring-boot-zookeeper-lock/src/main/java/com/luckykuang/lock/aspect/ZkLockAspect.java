package com.luckykuang.lock.aspect;

import com.luckykuang.lock.annotation.ZkLock;
import com.luckykuang.lock.service.CuratorDistributedLock;
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
 * Zookeeper分布式锁AOP切面
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ZkLockAspect {

    private final CuratorDistributedLock curatorLock;

    private final ExpressionParser parser = new SpelExpressionParser();
    private final ParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();

    @Around("@annotation(zkLock)")
    public Object around(ProceedingJoinPoint joinPoint, ZkLock zkLock) throws Throwable {
        // 1. 解析锁的key
        String lockKey = parseLockKey(joinPoint, zkLock);

        // 2. 尝试获取锁
        boolean locked = false;
        try {
            locked = curatorLock.tryLock(
                    lockKey,
                    zkLock.timeout(),
                    zkLock.timeUnit()
            );

            if (!locked) {
                return handleLockFailure(zkLock, lockKey);
            }

            log.info("获取分布式锁成功: {}", lockKey);

            // 3. 执行业务逻辑
            return joinPoint.proceed();

        } finally {
            // 4. 释放锁
            if (locked) {
                curatorLock.unlock(lockKey);
                log.info("释放分布式锁: {}", lockKey);
            }
        }
    }

    /**
     * 解析锁的key（支持SpEL表达式）
     */
    private String parseLockKey(ProceedingJoinPoint joinPoint, ZkLock zkLock) {
        String keyExpression = zkLock.key();
        String prefix = zkLock.prefix();

        // 检查是否包含SpEL表达式 #{...}
        int spelStart = keyExpression.indexOf("#{");
        if (spelStart == -1) {
            return prefix + keyExpression;
        }

        // 提取SpEL表达式部分
        int spelEnd = keyExpression.indexOf("}", spelStart);
        if (spelEnd == -1) {
            return prefix + keyExpression;
        }

        String spelExpression = keyExpression.substring(spelStart + 2, spelEnd);

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

        Expression expression = parser.parseExpression(spelExpression);
        Object value = expression.getValue(context);

        // 构建最终的key：前缀 + SpEL前固定部分 + SpEL求值结果 + SpEL后固定部分
        String beforeSpel = keyExpression.substring(0, spelStart);
        String afterSpel = keyExpression.substring(spelEnd + 1);
        return prefix + beforeSpel + value + afterSpel;
    }

    /**
     * 处理获取锁失败的情况
     */
    private Object handleLockFailure(ZkLock zkLock, String lockKey) {
        log.warn("获取分布式锁失败: {}", lockKey);

        return switch (zkLock.failStrategy()) {
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
