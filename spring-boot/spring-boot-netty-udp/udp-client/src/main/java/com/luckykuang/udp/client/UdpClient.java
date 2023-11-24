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

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author luckykuang
 * @date 2023/11/3 15:44
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UdpClient {

    /**
     * 打开UDP管道
     */
    public static Channel open() {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        try {
            Bootstrap clientBootstrap = new Bootstrap();
            clientBootstrap.group(eventLoopGroup)
                    .channel(NioDatagramChannel.class)
                    // 支持广播
                    .option(ChannelOption.SO_BROADCAST, true)
                    .handler(new UdpClientChannelInitializer());
            return clientBootstrap.bind(0).sync().channel();
        } catch (Exception e) {
            log.error("netty udp client exception",e);
            eventLoopGroup.shutdownGracefully();
            throw new RuntimeException("netty udp server exception:" + e.getMessage());
        }
    }
}
