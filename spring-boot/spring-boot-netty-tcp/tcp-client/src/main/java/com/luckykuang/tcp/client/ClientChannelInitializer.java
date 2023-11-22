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

import com.luckykuang.tcp.codec.TcpClientAsciiDecoder;
import com.luckykuang.tcp.codec.TcpClientAsciiEncoder;
import com.luckykuang.tcp.codec.TcpClientHexDecoder;
import com.luckykuang.tcp.codec.TcpClientHexEncoder;
import com.luckykuang.tcp.config.TcpClientConfig;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static com.luckykuang.tcp.constant.Constants.*;

/**
 * 初始化通道
 * @author luckykuang
 * @date 2023/8/24 16:50
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Resource
    private TcpClientConfig config;

    public void setTcpCodec(String code){
        TCP_CODEC.set(code);
    }

    /**
     * ChannelPipeline管道底层是责任链模式，所以特别是编码解码部分顺序一定不要搞错
     */
    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        // netty日志打印级别
        pipeline.addLast(new LoggingHandler(LogLevel.DEBUG));
        // 心跳机制,超时会触发Handle中userEventTrigger()方法
        pipeline.addLast(new IdleStateHandler(config.getReaderIdleTime(),config.getWriterIdleTime(),
                config.getAllIdleTime(),TimeUnit.MINUTES));
        String tcpCodec = TCP_CODEC.get();
        // 字符串编解码器
        if (StringUtils.isNotBlank(tcpCodec) && ASCII.equalsIgnoreCase(tcpCodec)){
            // 十进制编码/解码
            pipeline.addLast(new TcpClientAsciiEncoder(),new TcpClientAsciiDecoder());
        } else if (StringUtils.isNotBlank(tcpCodec) && HEX.equalsIgnoreCase(tcpCodec)) {
            // 十六进制编码/解码
            pipeline.addLast(new TcpClientHexEncoder(),new TcpClientHexDecoder());
        } else {
            log.warn("编码为空或不支持的编码：{}",tcpCodec);
            throw new RuntimeException("编码为空或不支持的编码");
        }
        // 自定义ChannelInboundHandlerAdapter
        pipeline.addLast(new ClientChannelInboundHandlerAdapter());
    }
}
