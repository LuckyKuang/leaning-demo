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

package com.luckykuang.udp.client;

import com.luckykuang.udp.codec.UdpClientAsciiDecoder;
import com.luckykuang.udp.codec.UdpClientAsciiEncoder;
import com.luckykuang.udp.codec.UdpClientHexDecoder;
import com.luckykuang.udp.codec.UdpClientHexEncoder;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import static com.luckykuang.udp.constant.Constants.*;

/**
 * @author luckykuang
 * @date 2023/11/3 17:30
 */
@Slf4j
@ChannelHandler.Sharable
public class UdpClientChannelInitializer extends ChannelInitializer<NioDatagramChannel> {

    public static void setUdpCodec(String code){
        UDP_CODEC.set(code);
    }

    @Override
    protected void initChannel(NioDatagramChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        // netty日志打印级别
        pipeline.addLast(new LoggingHandler(LogLevel.DEBUG));
        String udpCodec = UDP_CODEC.get();
        // 字符串编解码器
        if (StringUtils.isNotBlank(udpCodec) && ASCII.equalsIgnoreCase(udpCodec)){
            // 十进制编码/解码
            pipeline.addLast(new UdpClientAsciiEncoder(),new UdpClientAsciiDecoder());
        } else if (StringUtils.isNotBlank(udpCodec) && HEX.equalsIgnoreCase(udpCodec)) {
            // 十六进制编码/解码
            pipeline.addLast(new UdpClientHexEncoder(),new UdpClientHexDecoder());
        } else {
            log.warn("编码为空或不支持的编码：{}",udpCodec);
            throw new RuntimeException("编码为空或不支持的编码");
        }
        // 自定义处理器
        pipeline.addLast(new UdpClientChannelInboundHandler());
    }
}
