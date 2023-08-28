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

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author luckykuang
 * @date 2023/8/23 14:14
 */
@Slf4j
@Component
public class NettyTcpServer {
    /**
     * 处理Accept连接事件的线程，这里线程数设置为1即可，netty处理链接事件默认为单线程，过度设置反而浪费cpu资源
     */
    private final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    /**
     * 处理handler的工作线程，其实也就是处理IO读写。线程数据默认为 CPU 核心数乘以2
     */
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();

    @Resource
    private ServerChannelInitializer serverChannelInitializer;

    @Value("${netty.tcp.server.port}")
    private Integer port;

    /**
     * 与客户端建立连接后得到的通道对象
     */
    private Channel serverChannel;

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
            serverBootstrap.group(bossGroup, workerGroup);//组配置，初始化ServerBootstrap的线程组
            serverBootstrap.channel(NioServerSocketChannel.class);///构造channel通道工厂//bossGroup的通道，只是负责连接
            serverBootstrap.childHandler(serverChannelInitializer);//设置通道处理者ChannelHandlerworkerGroup的处理器
            serverBootstrap.option(ChannelOption.SO_BACKLOG, 1024);//socket参数，当服务器请求处理程全满时，用于临时存放已完成三次握手请求的队列的最大长度。如果未设置或所设置的值小于1，Java将使用默认值50。
            serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);//启用心跳保活机制，tcp，默认2小时发一次心跳
            // Future：异步任务的生命周期，可用来获取任务结果
            ChannelFuture channelFuture = serverBootstrap.bind(port).syncUninterruptibly();//绑定端口，开启监听，同步等待
            if (channelFuture != null && channelFuture.isSuccess()) {
                serverChannel = channelFuture.channel();//获取通道
                log.info("Netty tcp server start success, port = {}", port);
                serverChannel.closeFuture().syncUninterruptibly();
            } else {
                log.error("Netty tcp server start fail, port = {}", port);
            }
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
        if (serverChannel != null) {
            serverChannel.close();
        }
        try {
            if (!workerGroup.isShutdown()){
                Future<?> workerFuture = workerGroup.shutdownGracefully().await();
                if (!workerFuture.isSuccess()) {
                    log.error("netty tcp workerGroup shutdown fail, ", workerFuture.cause());
                }
            }
            if (!bossGroup.isShutdown()){
                Future<?> bossFuture = bossGroup.shutdownGracefully().await();
                if (!bossFuture.isSuccess()) {
                    log.error("netty tcp bossGroup shutdown fail, ", bossFuture.cause());
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Netty tcp server shutdown exception",e);
            throw new RuntimeException(e.getMessage());
        }
        log.info("Netty tcp server shutdown success");
    }
}
