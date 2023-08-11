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

package com.luckykuang.mqtt.controller;

import com.luckykuang.mqtt.publish.MqttPublish;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author luckykuang
 * @date 2023/8/11 11:28
 */
@RestController
@RequestMapping("mqtt")
@RequiredArgsConstructor
public class MqttController {

    private final MqttPublish mqttPublish;

    @GetMapping("publishMsg")
    public ResponseEntity<Boolean> publishMsg(String topic, String data, Integer qos){
        return ResponseEntity.ok(mqttPublish.publishMqttMessage(topic,data,qos));
    }

    @GetMapping("subscribeMsb")
    public void subscribeMsb(String topic, Integer qos){
        mqttPublish.subscribeMqttMessage(topic,qos);
    }

    @GetMapping("unsubscribeMsg")
    public void unsubscribeMsg(String topic){
        mqttPublish.unsubscribeMqttMessage(topic);
    }
}
