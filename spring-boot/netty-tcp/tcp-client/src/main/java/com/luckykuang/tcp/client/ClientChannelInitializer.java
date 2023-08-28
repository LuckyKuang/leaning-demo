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

package com.luckykuang.tcp.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 初始化通道
 * @author luckykuang
 * @date 2023/8/24 16:50
 */
@Component
@ChannelHandler.Sharable
public class ClientChannelInitializer extends ChannelInitializer<Channel> {

    @Value("${netty.tcp.client.readerIdleTime}")
    private long readerIdleTime;
    @Value("${netty.tcp.client.writerIdleTime}")
    private long writerIdleTime;
    @Value("${netty.tcp.client.allIdleTime}")
    private long allIdleTime;

    @Resource
    private ClientChannelInboundHandlerAdapter clientChannelInboundHandlerAdapter;

    @Override
    protected void initChannel(Channel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        // IdleStateHandler心跳机制,如果超时触发Handle中userEventTrigger()方法
        pipeline.addLast("idleStateHandler",new IdleStateHandler(
                readerIdleTime,
                writerIdleTime,
                allIdleTime,
                TimeUnit.MINUTES));
        // 字符串编解码器
        pipeline.addLast(
                new StringDecoder(CharsetUtil.UTF_8),
                new StringEncoder(CharsetUtil.UTF_8)
        );
        // 自定义ChannelInboundHandlerAdapter
        pipeline.addLast(clientChannelInboundHandlerAdapter);
    }
}
