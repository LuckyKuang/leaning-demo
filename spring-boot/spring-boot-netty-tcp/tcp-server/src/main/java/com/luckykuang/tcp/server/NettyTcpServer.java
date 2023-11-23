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

import com.luckykuang.tcp.config.TcpServerConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * netty tcp server
 * @author luckykuang
 * @date 2023/8/23 14:14
 */
@Slf4j
@Component
public class NettyTcpServer {
    /**
     * 处理Accept连接事件的线程
     */
    private final EventLoopGroup bossGroup = new NioEventLoopGroup();
    /**
     * 处理handler的工作线程
     */
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();

    @Resource
    private ServerChannelInitializer serverChannelInitializer;

    @Resource
    private TcpServerConfig config;

    /**
     * 开启Netty tcp server服务
     *
     * @return
     */
    @PostConstruct
    public void start() {
        try {
            // 启动类
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup);
            serverBootstrap.channel(NioServerSocketChannel.class);
            // 等待队列，当服务器请求处理程全满时，用于临时存放已完成三次握手请求的队列的最大长度。如果未设置或所设置的值小于1，Java将使用默认值50。
            serverBootstrap.option(ChannelOption.SO_BACKLOG, 1024);
            // 启用心跳保活机制，tcp，默认2小时发一次心跳
            serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            // 自定义处理器
            serverBootstrap.childHandler(serverChannelInitializer);
            // 绑定服务端端口，开启监听，同步等待
            ChannelFuture channelFuture = serverBootstrap.bind(config.getPort()).sync();
            if (channelFuture != null && channelFuture.isSuccess()) {
                log.info("Netty tcp server start success, port = {}", config.getPort());
                // 同步等待通道
                channelFuture.channel().closeFuture().sync();
            } else {
                log.error("Netty tcp server start fail, port = {}", config.getPort());
            }
        } catch (Exception e) {
            log.error("Netty tcp server open exception",e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    /**
     * 停止Netty tcp server服务
     */
    @PreDestroy
    public void destroy() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}
