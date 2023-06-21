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

/**
 * 6.双重检查【推荐使用】
 * 双重校验，实例添加volatile，保证了实例的原子性
 * 优点：线程安全；延迟加载；效率较高。
 * @author luckykuang
 * @date 2023/6/20 17:31
 */
public class Singleton6 {
    private static volatile Singleton6 INSTANCE;

    private Singleton6(){}

    public static Singleton6 getInstance(){
        if (INSTANCE == null){
            synchronized (Singleton6.class) {
                if (INSTANCE == null) {
                    INSTANCE = new Singleton6();
                }
            }
        }
        return INSTANCE;
    }
}
