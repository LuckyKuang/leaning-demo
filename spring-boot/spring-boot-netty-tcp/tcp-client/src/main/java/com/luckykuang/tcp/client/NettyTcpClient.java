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

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GlobalEventExecutor;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.luckykuang.tcp.constant.Constants.TCP_CACHE_CHANNEL;

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
     * 管道组
     */
    private static final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Resource
    private ClientChannelInitializer clientChannelInitializer;

    /**
     * 开启tcp client连接
     */
    public void open(String ip,int port, String codec) {
        clientChannelInitializer.setTcpCodec(codec);
        String sendCache = ip + ":" + port + "=" + codec;
        ChannelFuture channel = TCP_CACHE_CHANNEL.get(sendCache);
        if (channel != null && channel.channel().isActive()){
            log.info("已开启tcp client连接，无需再次连接");
            return;
        }
        try {
            // Bootstrap 是一个启动NIO服务的辅助启动类 用于客户端
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group);
            bootstrap.channel(NioSocketChannel.class);
            // 禁用nagle算法
            bootstrap.option(ChannelOption.TCP_NODELAY, true);
            // 允许地址和端口重用，即使该端口已经被绑定，服务端一点要加这个
            bootstrap.option(ChannelOption.SO_REUSEADDR, true);
            // 设置 I/O处理类,主要用于网络I/O事件，记录日志，编码、解码消息
            bootstrap.handler(clientChannelInitializer);
            // 连接服务端
            ChannelFuture channelFuture = bootstrap.connect(ip, port).sync();
            channelGroup.add(channelFuture.channel());
            TCP_CACHE_CHANNEL.put(sendCache,channelFuture);
            log.info("netty tcp client start success");
            // 等待连接端口关闭
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e){
            log.error("open tcp client exception",e);
            throw new RuntimeException("open tcp client exception");
        }
    }

    /**
     * 关闭指定管道
     * @param channel 需要关闭的管道
     * @return 成功-true 失败-false
     */
    public boolean close(Channel channel){
        for (Channel ch : channelGroup) {
            if (Objects.equals(channel,ch)){
                log.info("ChannelId:[{}] close success",ch.id().asLongText());
                return true;
            }
        }
        log.error("ChannelId:[{}] close fail",channel.id().asLongText());
        return false;
    }

    @PreDestroy
    public void destroy() {
        try {
            if (!group.isShutdown()){
                Future<?> groupFuture = group.shutdownGracefully().await();
                if (!groupFuture.isSuccess()) {
                    log.error("Netty tcp client group shutdown fail, ", groupFuture.cause());
                }else {
                    log.info("Netty tcp client group shutdown success");
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Netty tcp client shutdown exception",e);
            throw new RuntimeException(e.getMessage());
        }
    }
}
