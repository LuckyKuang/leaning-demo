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

package com.luckykuang.singleton;

import java.util.concurrent.atomic.AtomicReference;

/**
 * 线程安全的懒汉式单例模式 【改造】
 * 使用了 AtomicReference 来存储 entity 实例，并使用 updateAndGet 方法来获取实例。
 * 在 updateAndGet 方法中，我们检查 entity 是否为 null，如果不为 null，则直接返回该实例；
 * 否则，调用 createGsonInstance 方法创建新的 entity 实例。
 * 通过使用 AtomicReference 和 updateAndGet 方法，我们可以简化代码，并且仍然保持线程安全性，避免了显式的同步块。
 * @author luckykuang
 * @date 2023/9/26 13:40
 */
public class Singleton6New {
    private static final AtomicReference<Singleton6New> INSTANCE = new AtomicReference<>();

    public static Singleton6New getInstance() {
        return INSTANCE.updateAndGet(entity -> entity != null ? entity : createInstance());
    }

    private static Singleton6New createInstance() {
        return new Singleton6New();
    }
}
