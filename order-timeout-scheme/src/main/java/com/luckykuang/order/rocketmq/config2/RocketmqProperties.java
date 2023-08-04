///*
// * Copyright 2015-2023 the original author or authors.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      https://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.luckykuang.order.rocketmq.config2;
//
//import lombok.Data;
//
///**
// * 配置属性
// * @author luckykuang
// * @date 2023/8/3 11:32
// */
//@Data
//public class RocketmqProperties {
//    /**
//     * 消息主题
//     */
//    private String topic;
//    /**
//     * 消息组
//     */
//    private String group;
//    /**
//     * 消息标签
//     */
//    private String tag;
//    /**
//     * 消息发送超时时长，默认3s
//     */
//    private Long sendMessageTimeout = 3000L;
//    /**
//     * 同步发送消息失败重试次数，默认2
//     */
//    private Integer retryTimesWhenSendFailed = 2;
//    /**
//     * 异步发送消息失败重试次数，默认2
//     */
//    private Integer retryTimesWhenSendAsyncFailed = 2;
//}
