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

package com.luckykuang.order.rocketmq.consumer;

import com.luckykuang.order.rocketmq.vo.MqConsumerParam;
import com.luckykuang.order.rocketmq.vo.MqConsumerResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author luckykuang
 * @date 2023/8/4 17:32
 */
@Slf4j
@Component("mqConsumerService.topic1")
public class MqConsumerServiceTopic1 implements MqConsumerService{
    @Override
    public MqConsumerResult handle(MqConsumerParam param) {
        // 消费逻辑自行完善，简单写下
        List<MessageExt> list = param.getList();
        for (MessageExt messageExt : list) {
            String topic = messageExt.getTopic();
            String tag = messageExt.getTags();
            try {
                String msg = new String(messageExt.getBody(), StandardCharsets.UTF_8);
                log.info("消费者接收消息topic:{},tag:{},内容:{}", topic, tag, msg);
            } catch (Exception e) {
                log.error("消费消费失败,消息ID:" + messageExt.getMsgId(), e);
            }
        }
        return MqConsumerResult.success();
    }
}
