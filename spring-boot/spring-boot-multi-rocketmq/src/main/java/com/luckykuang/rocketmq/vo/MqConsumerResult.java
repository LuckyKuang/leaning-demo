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

package com.luckykuang.rocketmq.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author luckykuang
 * @date 2023/8/4 17:09
 */
@Data
@AllArgsConstructor
public class MqConsumerResult implements Serializable {
    @Serial
    private static final long serialVersionUID = 6228259334376885899L;

    /**
     * 是否处理成功
     */
    private boolean success = false;

    /**
     * 如果处理失败，是否允许消息队列继续调用，直到处理成功，默认true
     */
    private boolean reconsumeLater = true;

    /**
     * 是否需要记录消费日志，默认不记录
     */
    private boolean saveConsumeLog = false;

    /**
     * 错误消息
     */
    private String errMsg;

    /**
     * 错误堆栈
     */
    private Throwable e;

    public static MqConsumerResult success() {
        return new MqConsumerResult(true,true,false,null,null);
    }
}
