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

package com.luckykuang.tcp.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Optional;

/**
 * @author luckykuang
 * @date 2023/8/25 18:38
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TcpClientUtils {
    private static final ThreadLocal<Boolean> TCP_CONNECTED = new ThreadLocal<>();

    public static void setTcpConnected(boolean status){
        TCP_CONNECTED.set(status);
    }

    public static Optional<Boolean> getTcpConnected(){
        return Optional.ofNullable(TCP_CONNECTED.get());
    }

    public static void removeTcpConnected(){
        TCP_CONNECTED.remove();
    }
}