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

package com.luckykuang.order.rocketmq.config;

import com.luckykuang.order.rocketmq.vo.MqProducerVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author luckykuang
 * @date 2023/8/4 17:27
 */
@Slf4j
@Order(9)
@Component
@RequiredArgsConstructor
public class InitMqProducer implements ApplicationListener<ApplicationReadyEvent> {

    private final MqProducerConfig mqProducerConfig;

    /**
     * 存放所有生产者
     */
    public static Map<String, DefaultMQProducer> producerMap = new HashMap<>();

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        List<MqProducerVo> producerlist = mqProducerConfig.getProducers();
        if (CollectionUtils.isEmpty(producerlist)) {
            log.info("无MQ生产者---------------------------------------");
        }

        for (MqProducerVo vo : producerlist) {
            try {
                DefaultMQProducer producer = new DefaultMQProducer(vo.getGroupName());
                producer.setNamesrvAddr(vo.getNameServer());
                producer.setVipChannelEnabled(false);
                producer.setMaxMessageSize(vo.getMaxMessageSize());
                producer.setSendMsgTimeout(vo.getSendMessageTimeOut());
                producer.setRetryTimesWhenSendFailed(vo.getRetryTimesWhenSendFailed());
                producer.setRetryTimesWhenSendAsyncFailed(vo.getRetryTimesWhenSendFailed());
                producer.start();
                producerMap.put(vo.getProducerId(), producer);
                log.info("mq生产者{},{}启动成功", vo.getGroupName(), vo.getNameServer());
            } catch (MQClientException e) {
                log.error("mq生产者{},{}启动失败", vo.getGroupName(), vo.getNameServer(), e);
            }
        }
    }
}
