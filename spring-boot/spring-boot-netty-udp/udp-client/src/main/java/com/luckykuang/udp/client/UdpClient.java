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
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author luckykuang
 * @date 2023/11/3 15:44
 */
@Slf4j
public class UdpClient {
    private static final EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
    public static Channel channel;

    /**
     * 启动服务
     */
    public static void startup(int port) {
        try {
            Bootstrap clientBootstrap = new Bootstrap();
            clientBootstrap.group(eventLoopGroup)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .handler(new UdpClientChannelInitializer());
            ChannelFuture channelFuture = clientBootstrap.bind(port).sync();
            channel = channelFuture.channel();
            channelFuture.channel().closeFuture().await();
        } catch (Exception e) {
            log.error("netty udp client exception",e);
            throw new RuntimeException("netty udp server exception:" + e.getMessage());
        } finally {
            log.info("netty udp client close!");
            eventLoopGroup.shutdownGracefully();
        }
    }

    /**
     * 关闭服务
     */
    public static void shutdown() {
        eventLoopGroup.shutdownGracefully();
    }
}
