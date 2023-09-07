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
 * 4.懒汉式（线程安全，同步方法）【不推荐用】
 * 缺点：效率太低了，每个线程在想获得类的实例时候，执行getInstance()方法都要进行同步。
 * @author luckykuang
 * @date 2023/6/20 17:28
 */
public class Singleton4 {
    private static Singleton4 INSTANCE;

    private Singleton4(){}

    public static synchronized Singleton4 getInstance(){
        if (INSTANCE == null){
            INSTANCE = new Singleton4();
        }
        return INSTANCE;
    }
}
