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

import com.luckykuang.udp.config.UdpServerConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author luckykuang
 * @date 2023/11/3 15:44
 */
@Slf4j
@Component
public class UdpServer {

    @Resource
    private UdpServerConfig config;

    @Resource
    private UdpServerChannelInitializer udpServerChannelInitializer;

    /**
     * 启动服务
     */
    @PostConstruct
    public void open() {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        try {
            Bootstrap serverBootstrap = new Bootstrap();
            serverBootstrap.group(eventLoopGroup);
            serverBootstrap.channel(NioDatagramChannel.class);
            serverBootstrap.option(ChannelOption.SO_BROADCAST, true);
            serverBootstrap.handler(udpServerChannelInitializer);
            ChannelFuture channelFuture = serverBootstrap.bind(config.getPort()).sync();
            if (channelFuture != null && channelFuture.isSuccess()) {
                log.info("Netty udp server start success, port = {}", config.getPort());
                // 同步等待通道
                channelFuture.channel().closeFuture().await();
            } else {
                log.error("Netty udp server start fail, port = {}", config.getPort());
            }
        } catch (Exception e) {
            log.error("netty udp server exception port:{}",config.getPort(),e);
            throw new RuntimeException("netty udp server exception:" + e.getMessage());
        } finally {
            log.info("netty udp server shutdown!");
            eventLoopGroup.shutdownGracefully();
        }
    }
}
