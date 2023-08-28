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

package com.luckykuang.tcp.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author luckykuang
 * @date 2023/8/23 14:55
 */
@Component
public class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Value("${netty.tcp.server.readerIdleTime}")
    private long readerIdleTime;
    @Value("${netty.tcp.server.writerIdleTime}")
    private long writerIdleTime;
    @Value("${netty.tcp.server.allIdleTime}")
    private long allIdleTime;

    @Resource
    private ServerChannelHandler serverChannelHandler;

    @Override
    protected void initChannel(SocketChannel socketChannel) {
        ChannelPipeline pipeline = socketChannel.pipeline();
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
        // 自定义Handler
        pipeline.addLast("serverChannelHandler", serverChannelHandler);
    }
}
