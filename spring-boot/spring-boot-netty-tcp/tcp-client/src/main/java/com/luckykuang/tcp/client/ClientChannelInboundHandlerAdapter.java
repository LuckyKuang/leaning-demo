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

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.net.InetSocketAddress;

import static com.luckykuang.tcp.constant.Constants.TCP_RESPONSE_DATA;
import static com.luckykuang.tcp.constant.Constants.TCP_SEND_CACHE;

/**
 * 通道具体实现
 * @author luckykuang
 * @date 2023/8/24 16:52
 */
@Slf4j
public class ClientChannelInboundHandlerAdapter extends SimpleChannelInboundHandler<String> {

    /**
     * 从服务端收到新的数据时，这个方法会在收到消息时被调用
     */
    @Override
    public void channelRead0(ChannelHandlerContext ctx, String msg) {
        log.info("ChannelId:[{}],收到服务端消息返回：[{}]", ctx.channel().id().asLongText(),msg);
        // 此处用于判断该管道是否发送消息，用于过滤连接服务器后，服务器返回的消息
        String sendCache = TCP_SEND_CACHE.get(ctx.channel().id().asLongText());
        if (StringUtils.isNotBlank(sendCache)) {
            // 将收到的消息写入缓存，在业务端去读取
            // 此处也可以替换成websocket来与前端交互，消息回复更加及时和准确
            TCP_RESPONSE_DATA.put(ctx.channel().id().asLongText(),msg);
        }
    }

    /**
     * 客户端与服务端第一次建立连接时 执行
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        InetSocketAddress inSocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String serverIp = inSocket.getAddress().getHostAddress();
        log.info("服务端已连接："+serverIp);
    }

    /**
     * 客户端与服务端连接断开时 执行
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        InetSocketAddress inSocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String serverIp = inSocket.getAddress().getHostAddress();
        ctx.close(); // 断开连接时，必须关闭，否则造成资源浪费
        log.info("服务端已断开：:"+serverIp);
    }

    /**
     * 从服务端收到新的数据、读取完成时调用
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        InetSocketAddress inSocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String serverIp = inSocket.getAddress().getHostAddress();
        log.info("channelReadComplete serverIp:{}",serverIp);
        ctx.flush();
    }

    /**
     * 当出现 Throwable 对象才会被调用，即当 Netty 由于 IO 错误或者处理器在处理事件时抛出的异常时
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("exceptionCaught",cause);
        ctx.close();// 抛出异常，断开与客户端的连接
    }

    /**
     * 心跳机制，超时处理
     *
     * @param ctx
     * @param event
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object event) throws Exception {
        InetSocketAddress inSocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String serverIp = inSocket.getAddress().getHostAddress();
        if (event instanceof IdleStateEvent idleStateEvent) {
            if (idleStateEvent.state() == IdleState.READER_IDLE) {
                log.info("serverIp: " + serverIp + " 读超时");
                ctx.disconnect();//断开
            } else if (idleStateEvent.state() == IdleState.WRITER_IDLE) {
                log.info("serverIp: " + serverIp + " 写超时");
                ctx.disconnect();
            } else if (idleStateEvent.state() == IdleState.ALL_IDLE) {
                log.info("serverIp: " + serverIp + " 读写超时");
                ctx.disconnect();
            }
        }
    }
}
