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
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

/**
 * @author luckykuang
 * @date 2023/8/24 16:52
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class ClientChannelInboundHandlerAdapter extends SimpleChannelInboundHandler<Object> {

    /**
     * 从服务端收到新的数据时，这个方法会在收到消息时被调用
     */
    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) {
        if(msg == null){
            return;
        }
        if (msg instanceof String received){
            log.info("收到字符串消息：{}", received);
            // 响应
            ctx.channel().writeAndFlush("response msg："+received).syncUninterruptibly();
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
        TcpClientUtils.removeTcpConnected();
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
        log.info("exceptionCaught");
        cause.printStackTrace();
        TcpClientUtils.removeTcpConnected();
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
                log.info("serverIp: " + serverIp + " READER_IDLE 读超时");
                ctx.disconnect();//断开
            } else if (idleStateEvent.state() == IdleState.WRITER_IDLE) {
                log.info("serverIp: " + serverIp + " WRITER_IDLE 写超时");
                ctx.disconnect();
            } else if (idleStateEvent.state() == IdleState.ALL_IDLE) {
                log.info("serverIp: " + serverIp + " ALL_IDLE 总超时");
                ctx.disconnect();
            }
        }
    }
}
