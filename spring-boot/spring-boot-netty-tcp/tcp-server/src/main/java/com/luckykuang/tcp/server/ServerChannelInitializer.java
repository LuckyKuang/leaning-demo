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

import com.luckykuang.tcp.codec.TcpServerAsciiDecoder;
import com.luckykuang.tcp.codec.TcpServerAsciiEncoder;
import com.luckykuang.tcp.codec.TcpServerHexDecoder;
import com.luckykuang.tcp.codec.TcpServerHexEncoder;
import com.luckykuang.tcp.config.TcpServerConfig;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 初始化通道
 * @author luckykuang
 * @date 2023/8/23 14:55
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Resource
    private TcpServerConfig config;

    /**
     * ChannelPipeline管道底层是责任链模式，所以特别是编码解码部分顺序一定不要搞错
     * 服务端需要什么编码/解码，就用哪个编码/解码
     */
    @Override
    protected void initChannel(SocketChannel socketChannel) {
        ChannelPipeline pipeline = socketChannel.pipeline();
        // 心跳机制,超时会触发Handle中userEventTrigger()方法
        pipeline.addLast(new IdleStateHandler(config.getReaderIdleTime(),config.getWriterIdleTime(),
                config.getAllIdleTime(),TimeUnit.MINUTES));
        String codec = config.getCodec();
        // 字符串编解码器
        if ("ascii".equalsIgnoreCase(codec)) {
            // 十进制编码/解码
            pipeline.addLast(new TcpServerAsciiEncoder(), new TcpServerAsciiDecoder());
        } else if ("hex".equalsIgnoreCase(codec)) {
            // 十六进制编码/解码
            pipeline.addLast(new TcpServerHexEncoder(),new TcpServerHexDecoder());
        } else {
            log.warn("编码为空或不支持的编码：{}，请检查配置",codec);
            throw new RuntimeException("编码为空或不支持的编码，请检查配置");
        }

        // 自定义Handler
        pipeline.addLast(new ServerChannelInboundHandler());
    }
}
