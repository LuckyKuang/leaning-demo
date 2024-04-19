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

package com.luckykuang.controller;

import com.luckykuang.annotation.Limit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author luckykuang
 * @date 2024/4/15 16:38
 */
@Slf4j
@RestController
@RequestMapping("test")
public class TestController {

    @Limit(key = "cachingTest", permitsPerSecond = 1, timeout = 500, msg = "当前排队人数较多，请稍后再试！")
    @GetMapping("cachingTest")
    public List<String> cachingTest(){
        log.info("------读取本地------");
        List<String> list = new ArrayList<>();
        list.add("蜡笔小新");
        list.add("哆啦A梦");
        list.add("四驱兄弟");

        return list;
    }
}
