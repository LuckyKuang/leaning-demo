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

import com.luckykuang.tcp.util.TcpClientUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * netty 客户端
 * @author luckykuang
 * @date 2023/8/24 16:49
 */
@Slf4j
@Component
public class NettyTcpClient {

    /**
     * 客户端的NIO线程组
     */
    EventLoopGroup group = new NioEventLoopGroup();
    /**
     * 与服务端建立连接后得到的通道对象
     */
    private Channel clientChannel;

    @Value("${netty.tcp.client.ip}")
    private String ip;
    @Value("${netty.tcp.client.port}")
    private Integer port;

    @Resource
    private ClientChannelInitializer clientChannelInitializer;

    @PostConstruct
    public void connect(){
        try {
            start();
        } catch (Exception e){
            log.info("服务端已断开连接，等待重连：{}",System.currentTimeMillis());
        }
    }

    /**
     * 开启tcp client连接
     */
    public void start() throws Exception {
        // Bootstrap 是一个启动NIO服务的辅助启动类 用于客户端
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group);
        bootstrap.channel(NioSocketChannel.class);
        // 设置 I/O处理类,主要用于网络I/O事件，记录日志，编码、解码消息
        bootstrap.handler(clientChannelInitializer);
        // 心跳保活
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        // 连接服务端
        ChannelFuture channelFuture = bootstrap.connect(ip, port).sync();
        if (channelFuture != null && channelFuture.isSuccess()){
            // tcp连接成功
            TcpClientUtils.setTcpConnected(true);
            clientChannel = channelFuture.channel();
            log.info("netty client start success");
            // 等待连接端口关闭
            channelFuture.channel().closeFuture().sync();
        }
    }

    @PreDestroy
    public void destroy() {
        if (clientChannel != null) {
            TcpClientUtils.removeTcpConnected();
            clientChannel.close();
        }
        if (!group.isShutdown()){
            try {
                Future<?> groupFuture = group.shutdownGracefully().await();
                if (!groupFuture.isSuccess()) {
                    log.error("Netty tcp client groupFuture shutdown fail, ", groupFuture.cause());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Netty tcp client shutdown exception",e);
                throw new RuntimeException(e.getMessage());
            }
        }
        log.info("Netty tcp client shutdown success");
    }
}
