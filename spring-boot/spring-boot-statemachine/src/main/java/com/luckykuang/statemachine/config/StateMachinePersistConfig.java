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

package com.luckykuang.statemachine.config;

import com.google.gson.Gson;
import com.luckykuang.statemachine.enums.OrderStatus;
import com.luckykuang.statemachine.enums.OrderStatusChangeEvent;
import com.luckykuang.statemachine.util.GsonUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.data.redis.RedisStateMachineContextRepository;
import org.springframework.statemachine.data.redis.RedisStateMachinePersister;
import org.springframework.statemachine.persist.DefaultStateMachinePersister;
import org.springframework.statemachine.persist.RepositoryStateMachinePersist;
import org.springframework.statemachine.persist.StateMachinePersister;

import java.util.HashMap;
import java.util.Map;

/**
 * 状态机持久化配置
 * @author luckykuang
 * @date 2023/10/9 16:23
 */
@Slf4j
@Configuration
public class StateMachinePersistConfig<E, S> {

    private static final Gson GSON = GsonUtils.getGsonInstance();

    private static final Map<Object,StateMachineContext<OrderStatus, OrderStatusChangeEvent>>
            STATE_MACHINE_CONTEXT_MAP = new HashMap<>();

    @Resource
    private RedisConnectionFactory redisConnectionFactory;

    /**
     * 持久化到内存map中
     * @return
     */
    @Bean(name = "stateMachineMemPersister")
    public static StateMachinePersister<OrderStatus, OrderStatusChangeEvent,
            StateMachineContext<OrderStatus, OrderStatusChangeEvent>> getPersisted() {
        return new DefaultStateMachinePersister<>(new StateMachinePersist<>() {

            @Override
            public void write(StateMachineContext<OrderStatus, OrderStatusChangeEvent> context,
                              StateMachineContext<OrderStatus, OrderStatusChangeEvent> contextObj) throws Exception {
                log.info("持久化状态机,context:{},contextObj:{}", GSON.toJson(context), GSON.toJson(contextObj));
                STATE_MACHINE_CONTEXT_MAP.put(contextObj, context);
            }

            @Override
            public StateMachineContext<OrderStatus, OrderStatusChangeEvent> read(
                    StateMachineContext<OrderStatus, OrderStatusChangeEvent> contextObj) throws Exception {
                log.info("获取状态机,contextObj:{}", GSON.toJson(contextObj));
                StateMachineContext<OrderStatus, OrderStatusChangeEvent> stateMachineContext =
                        STATE_MACHINE_CONTEXT_MAP.get(contextObj);
                log.info("获取状态机结果,stateMachineContext:{}", GSON.toJson(stateMachineContext));
                return stateMachineContext;
            }
        });
    }

    /**
     * 持久化到redis中，在分布式系统中使用
     * @return
     */
    @Bean(name = "stateMachineRedisPersister")
    public RedisStateMachinePersister<E, S> getRedisPersisted() {
        RedisStateMachineContextRepository<E, S> repository =
                new RedisStateMachineContextRepository<>(redisConnectionFactory);
        RepositoryStateMachinePersist<E, S> p = new RepositoryStateMachinePersist<>(repository);
        return new RedisStateMachinePersister<>(p);
    }
}
