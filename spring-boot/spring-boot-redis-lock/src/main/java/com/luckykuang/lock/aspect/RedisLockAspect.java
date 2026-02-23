package com.luckykuang.lock.aspect;

import com.luckykuang.lock.annotation.RedisLock;
import com.luckykuang.lock.service.ReentrantRedisLock;
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
 * Redis分布式锁AOP切面
 *
 * @author LuckyKuang
 * @since 2025-01-15
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RedisLockAspect {

    private final ReentrantRedisLock reentrantRedisLock;

    private final ExpressionParser parser = new SpelExpressionParser();
    private final ParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();

    @Around("@annotation(redisLock)")
    public Object around(ProceedingJoinPoint joinPoint, RedisLock redisLock) throws Throwable {
        // 1. 解析锁的key
        String lockKey = parseLockKey(joinPoint, redisLock);

        // 2. 尝试获取锁
        boolean locked = false;
        try {
            locked = reentrantRedisLock.tryLock(
                    lockKey,
                    redisLock.expireTime(),
                    redisLock.timeUnit()
            );

            if (!locked) {
                log.warn("获取分布式锁失败: {}", lockKey);
                return handleLockFailure(redisLock, lockKey);
            }

            log.info("获取分布式锁成功: {}", lockKey);

            // 3. 执行业务逻辑
            return joinPoint.proceed();

        } catch (Exception e) {
            log.error("执行业务逻辑异常", e);
            throw e;
        } finally {
            // 4. 释放锁
            if (locked) {
                boolean unlocked = reentrantRedisLock.unlock(lockKey);
                log.info("释放分布式锁 {}: {}", lockKey, unlocked ? "成功" : "失败");
            }
        }
    }

    /**
     * 解析锁的key（支持SpEL表达式）
     */
    private String parseLockKey(ProceedingJoinPoint joinPoint, RedisLock redisLock) {
        String keyExpression = redisLock.key();
        String prefix = redisLock.prefix();

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

        // 如果SpEL表达式已经返回了完整的key（包含前缀），直接返回
        // 否则添加前缀
        if (value != null && (value.contains(":") || prefix.isEmpty())) {
            return value;
        }
        return prefix + value;
    }

    /**
     * 处理获取锁失败的情况
     */
    private Object handleLockFailure(RedisLock redisLock, String lockKey) {
        log.warn("获取分布式锁失败: {}", lockKey);

        return switch (redisLock.failStrategy()) {
            case THROW_EXCEPTION ->
                throw new RuntimeException("获取分布式锁失败: " + lockKey);
            case FAIL_FAST -> {
                // 快速失败，返回null
                // 实际项目中可以返回统一响应对象
                yield null;
            }
            case IGNORE -> {
                // 忽略，继续执行（不推荐）
                log.warn("忽略锁获取失败，继续执行");
                yield null;
            }
        };
    }
}
