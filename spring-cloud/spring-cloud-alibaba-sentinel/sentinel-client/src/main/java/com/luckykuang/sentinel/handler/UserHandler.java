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

package com.luckykuang.sentinel.handler;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author luckykuang
 * @date 2023/7/17 18:14
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserHandler {

    /**
     * 限流/熔断降级：根据sentinel配置的阈值，超过即触发
     */
    public static String getUserBlockHandler(String name, BlockException blockException){
        log.warn("限流/熔断业务处理"+"-"+name,blockException);
        return "限流/熔断业务处理"+"-"+name;
    }

    /**
     * 异常处理：接口调用异常即触发
     */
    public static String getUserHandlerFallback(String name,Throwable throwable){
        log.error("异常业务处理"+"-"+name,throwable);
        return "异常业务处理"+"-"+name;
    }
}
