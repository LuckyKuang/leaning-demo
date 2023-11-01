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

package com.luckykuang.netty.server;

import com.luckykuang.netty.config.MqttServerConfig;
import com.luckykuang.netty.handler.MqttServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author luckykuang
 * @date 2023/10/30 15:22
 */
@Slf4j
@Component
public class MqttServer {
    @Resource
    private MqttServerConfig mqttServerConfig;
    private static final EventLoopGroup bossGroup = new NioEventLoopGroup();
    private static final EventLoopGroup workerGroup = new NioEventLoopGroup();

    @PostConstruct
    public void initMqttServer() {
        new Thread(() -> {
            try {
                start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    @PreDestroy
    public void destroyMqttServer(){
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    /**
     * backlog 设置注意点
     * 服务器TCP内核 内维护了两个队列，称为A(未连接队列)和B(已连接队列)
     * 如果A+B的长度大于Backlog时，新的连接就会被TCP内核拒绝掉。
     * 所以，如果backlog过小，就可能出现Accept的速度跟不上，A,B队列满了，就会导致客户端无法建立连接。
     * 需要注意的是，backlog对程序的连接数没影响，但是影响的是还没有被Accept取出的连接。
     * @throws Exception
     */
    public void start() throws Exception {
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                // 设置非阻塞,用它来建立新accept的连接,用于构造ServerSocketChannel的工厂类
                .channel(NioServerSocketChannel.class)
                // 禁用nagle算法
                // Nagle算法试图减少TCP包的数量和结构性开销, 将多个较小的包组合成较大的包进行发送
                // 这个算法受TCP延迟确认影响, 会导致相继两次向连接发送请求包,读数据时会有一个最多达500毫秒的延时.
                .childOption(ChannelOption.TCP_NODELAY, mqttServerConfig.getTcpNodeLay())
                // 临时存放已完成三次握手的请求的队列的最大长度。
                // 如果未设置或所设置的值小于1，Java将使用默认值50。
                // 如果大于队列的最大长度，请求会被拒绝
                .option(ChannelOption.SO_BACKLOG, mqttServerConfig.getBackLog())
                .childOption(ChannelOption.SO_KEEPALIVE, mqttServerConfig.getKeepAlive())
                .handler(new LoggingHandler(mqttServerConfig.getLogLevel()))
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        // Netty提供的心跳检测 服务端30秒，客户端ping心跳要配置为10秒
                        ch.pipeline().addFirst("idle", new IdleStateHandler(
                                mqttServerConfig.getReaderIdleTime(),
                                mqttServerConfig.getWriterIdleTime(),
                                mqttServerConfig.getAllIdleTime()));
                        ch.pipeline().addLast("decoder", new MqttDecoder());
                        ch.pipeline().addLast("encoder", MqttEncoder.INSTANCE);
                        ch.pipeline().addLast("handler", new MqttServerHandler());
                    }
                });
        Channel ch = b.bind(mqttServerConfig.getServerPort()).sync().channel();
        log.info("Netty MQTT server started on port:{}",mqttServerConfig.getServerPort());
        ch.closeFuture().sync();
    }
}
