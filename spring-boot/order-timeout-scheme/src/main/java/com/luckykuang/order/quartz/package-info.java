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

/**
 * 方案描述：通过定时任务的方式去轮询扫描数据库表，根据订单有效期来判断订单是否到期，到期则更新订单状态。这里我们使用quartz作业调度框架来实现定时轮询。
 * 优点：这种方案优点是实现简单，通过quartz框架进行任务调度，无其他依赖，支持集群部署。
 * 缺点：简单粗暴的全表扫描方式对数据库性能影响特别大，可能影响其他正常的业务操作响应时效，另外配置扫描时间间隔也是个问题，配置大了，扫描延迟，
 *      影响取消订单的精准时间，在数据量较大的情况下，配置小了影响数据库性能，所以需要根据实际情况进行评估。
 */
package com.luckykuang.order.quartz;