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

package com.luckykuang.rocketmq.listener;

import com.luckykuang.rocketmq.consumer.EnhanceMessageHandler;
import com.luckykuang.rocketmq.entity.MemberMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * @author luckykuang
 * @date 2023/10/22 22:26
 */
@Slf4j
@Component
@RocketMQMessageListener(
        consumerGroup = "enhance_consumer_group",
        topic = "rocket_enhance",
        // 控制选择哪个消息，默认"*"
        selectorExpression = "*",
        // 默认是20个线程并发消息，配置 consumeThreadNumber 参数指定并发消费者线程数，避免太大导致资源不够
        consumeThreadNumber = 5
)
public class MemberMessageListener extends EnhanceMessageHandler<MemberMessage> implements RocketMQListener<MemberMessage> {
    @Override
    protected void handleMessage(MemberMessage message) throws Exception {
        // 此时这里才是最终的业务处理，代码只需要处理资源类关闭异常，其他的可以交给父类重试
        System.out.println("业务消息处理:"+message.getUserName());
    }

    @Override
    protected void handleMaxRetriesExceeded(MemberMessage message) {
        // 当超过指定重试次数消息时此处方法会被调用
        // 生产中可以进行回退或其他业务操作
        log.error("消息消费失败，请执行后续处理");
    }


    /**
     * 是否执行重试机制
     */
    @Override
    protected boolean isRetry() {
        return true;
    }

    @Override
    protected boolean throwException() {
        // 是否抛出异常，false搭配retry自行处理异常
        return false;
    }

    @Override
    protected boolean filter(MemberMessage message) {
        // 消息过滤
        return false;
    }

    /**
     * 监听消费消息，不需要执行业务处理，委派给父类做基础操作，父类做完基础操作后会调用子类的实际处理类型
     */
    @Override
    public void onMessage(MemberMessage memberMessage) {
        super.dispatchMessage(memberMessage);
    }
}
