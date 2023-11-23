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

package com.luckykuang.udp.server;

import com.luckykuang.udp.codec.UdpServerAsciiDecoder;
import com.luckykuang.udp.codec.UdpServerHexDecoder;
import com.luckykuang.udp.config.UdpServerConfig;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author luckykuang
 * @date 2023/11/3 16:59
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class UdpServerChannelInitializer extends ChannelInitializer<NioDatagramChannel> {

    @Resource
    private UdpServerConfig config;

    @Override
    protected void initChannel(NioDatagramChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        // 定义netty日志级别，用于调试
        pipeline.addLast(new LoggingHandler(LogLevel.DEBUG));
        String codec = config.getCodec();
        // 字符串编解码器
        if ("ascii".equalsIgnoreCase(codec)) {
            // 十进制解码
            pipeline.addLast(new UdpServerAsciiDecoder());
        } else if ("hex".equalsIgnoreCase(codec)) {
            // 十六进制解码
            pipeline.addLast(new UdpServerHexDecoder());
        } else {
            log.warn("编码为空或不支持的编码：{}，请检查配置",codec);
            throw new RuntimeException("编码为空或不支持的编码，请检查配置");
        }
        // 自定义处理器
        ch.pipeline().addLast(new UdpServerChannelInboundHandler());
    }
}
