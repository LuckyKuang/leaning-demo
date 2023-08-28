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

import com.luckykuang.tcp.util.TcpServerChannelUtils;
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
 * @date 2023/8/23 14:19
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class ServerChannelHandler extends SimpleChannelInboundHandler<Object> {

    /**
     * 拿到传过来的msg数据，开始处理
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("Netty tcp server receive msg : {}", msg);
        if (msg == null){
            return;
        }
        if (msg instanceof String received){
            log.info("收到字符串消息：{}", received);
            // 响应
            ctx.channel().writeAndFlush("response msg：" + received).syncUninterruptibly();
        }
    }

    /**
     * 活跃的、有效的通道
     * 第一次连接成功后进入的方法
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        InetSocketAddress inSocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIp = inSocket.getAddress().getHostAddress();
        log.info("tcp clientIp: [" + clientIp + "] connect success");
        // 往channel map中添加channel信息
        TcpServerChannelUtils.addChannel(ctx.channel());
    }

    /**
     * 不活动的通道
     * 连接丢失后执行的方法（client端可据此实现断线重连）
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        InetSocketAddress inSocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIp = inSocket.getAddress().getHostAddress();
        log.info("tcp clientIp: [" + clientIp + "] connect success");
        // 删除Channel Map中的失效Client
        TcpServerChannelUtils.removeChannel(ctx.channel());
        // 断开连接时关闭，避免资源浪费
        ctx.close();
    }

    /**
     * 异常处理
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        InetSocketAddress inSocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIp = inSocket.getAddress().getHostAddress();
        //发生异常，关闭连接
        log.error("客户端地址：{}，连接id：{}的通道发生异常，即将断开连接", clientIp, ctx.channel().id().asShortText());
        ctx.close();//再次建议close
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
        String clientIp = inSocket.getAddress().getHostAddress();
        if (event instanceof IdleStateEvent idleStateEvent) {
            if (idleStateEvent.state() == IdleState.READER_IDLE) {
                log.info("ClientIp: " + clientIp + " READER_IDLE 读超时");
                ctx.disconnect();//断开
            } else if (idleStateEvent.state() == IdleState.WRITER_IDLE) {
                log.info("ClientIp: " + clientIp + " WRITER_IDLE 写超时");
                ctx.disconnect();
            } else if (idleStateEvent.state() == IdleState.ALL_IDLE) {
                log.info("ClientIp: " + clientIp + " ALL_IDLE 总超时");
                ctx.disconnect();
            }
        }
    }
}