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

package com.luckykuang.netty.websocket.runner;

import com.luckykuang.netty.exception.BusinessException;
import com.luckykuang.netty.util.ThreadPoolUtils;
import com.luckykuang.netty.websocket.handler.WebsocketMessageHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

/**
 * 初始化netty服务
 * ApplicationRunner：跟随Springboot同时启动
 * ApplicationListener#ApplicationContextEvent：监听spring上下文引发的事件，参见：{@link org.springframework.context.event.ApplicationContextEvent}
 * @author luckykuang
 * @date 2023/8/21 9:59
 */
@Slf4j
@Component
public class NettyBootstrapRunner implements ApplicationRunner, ApplicationListener<ContextClosedEvent> {

    @Value("${netty.websocket.port}")
    private int port;

    @Value("${netty.websocket.ip}")
    private String ip;

    @Value("${netty.websocket.path}")
    private String path;

    @Value("${netty.websocket.allow-extensions}")
    private boolean allowExtensions;

    @Value("${netty.websocket.max-frame-size}")
    private int maxFrameSize;

    @Value("${netty.websocket.handshake-timeout-millis}")
    private long handshakeTimeoutMillis;

    private Channel serverChannel;

    private final WebsocketMessageHandler websocketMessageHandler;

    public NettyBootstrapRunner(WebsocketMessageHandler websocketMessageHandler) {
        this.websocketMessageHandler = websocketMessageHandler;
    }

    @Override
    public void run(ApplicationArguments args) {
        ThreadPoolUtils.execute(this::start);
    }

    @Override
    public void onApplicationEvent(@NotNull ContextClosedEvent event) {
        log.info("websocket 服务停止");
        if (this.serverChannel != null) {
            this.serverChannel.close();
        }
    }

    private void start(){
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup);
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.localAddress(new InetSocketAddress(this.ip, this.port));
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) {
                    ChannelPipeline pipeline = socketChannel.pipeline();
                    // 默认解码器
                    pipeline.addLast(new HttpServerCodec());
                    // 数据块的方式处理，用于大数据的分区传输
                    pipeline.addLast(new ChunkedWriteHandler());
                    // 聚合器，使用websocket会用到
                    pipeline.addLast(new HttpObjectAggregator(65536));
                    // 将操作转发给ChannelPipeline中的下一个ChannelHandler
                    pipeline.addLast(new ChannelInboundHandlerAdapter() {
                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            if(msg instanceof FullHttpRequest fullHttpRequest) {
                                String uri = fullHttpRequest.uri();
                                if (!uri.equals(path)) {
                                    // 访问的路径不是 websocket的端点地址，响应404
                                    ctx.channel().writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND))
                                            .addListener(ChannelFutureListener.CLOSE);
                                    return ;
                                }
                            }
                            super.channelRead(ctx, msg);
                        }
                    });
                    // websocket默认压缩处理
                    pipeline.addLast(new WebSocketServerCompressionHandler());
                    // websocket服务端握手协议处理
                    pipeline.addLast(new WebSocketServerProtocolHandler(path, null, allowExtensions, maxFrameSize, handshakeTimeoutMillis));
                    // 消息处理
                    pipeline.addLast(websocketMessageHandler);
                }
            });
            Channel channel = serverBootstrap.bind().sync().channel();
            this.serverChannel = channel;
            log.info("websocket 服务启动 ip={},port={}", this.ip, this.port);
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("websocket 服务启动异常",e);
            // 如果抛异常，中断该线程
            Thread.currentThread().interrupt();
            throw new BusinessException("websocket 服务启动异常");
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
