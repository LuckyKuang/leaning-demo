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

package com.luckykuang.netty.websocket.handler;

import com.luckykuang.netty.service.DiscardService;
import com.luckykuang.netty.util.WebsocketChannelUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.Date;

import static io.netty.handler.codec.http.HttpUtil.isKeepAlive;

/**
 * websocket消息处理
 * @author luckykuang
 * @date 2023/8/21 9:59
 */
@Slf4j
@ChannelHandler.Sharable // 表明可以将带注释的ChannelHandler的同一个实例多次添加到一个或多个ChannelPipeline中，而不需要竞争条件。
@Component
public class WebsocketMessageHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    private WebSocketServerHandshaker handshaker;

    @Resource
    private DiscardService discardService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg) throws Exception {
        if (msg instanceof FullHttpRequest fullHttpRequest){
            // 以http请求形式接入，但是走的是websocket
            handleHttpRequest(ctx, fullHttpRequest);
        } else {
            // 处理websocket客户端发来的消息
            handlerWebSocketFrame(ctx,msg);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        InetSocketAddress inSocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIp = inSocket.getAddress().getHostAddress();
        log.info("客户端加入连接 id:{},clientIp:{}", ctx.channel().id(), clientIp);
        WebsocketChannelUtils.addChannel(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        InetSocketAddress inSocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIp = inSocket.getAddress().getHostAddress();
        log.info("客户端断开连接 id:{},clientIp:{}", ctx.channel().id(), clientIp);
        WebsocketChannelUtils.removeChannel(ctx.channel());
        // 客户端断开连接时关闭，避免资源浪费
        ctx.close();
    }

    /**
     * 服务端收到客户端消息，读取完成时调用
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        log.info("channelReadComplete");
        ctx.flush();
    }

    private void handlerWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame){
        // 判断是否关闭链路的指令
        if (frame instanceof CloseWebSocketFrame closeWebSocketFrame) {
            handshaker.close(ctx.channel(), closeWebSocketFrame.retain());
        }
        // 判断是否ping消息
        else if (frame instanceof PingWebSocketFrame pingWebSocketFrame) {
            ctx.channel().write(pingWebSocketFrame.content().retain());
        }
        // 支持的文本消息
        else if (frame instanceof TextWebSocketFrame textWebSocketFrame) {
            // 接收到客户端发来的消息
            String request = textWebSocketFrame.text();
            log.debug("服务端收到消息：" + request);
            TextWebSocketFrame tws = new TextWebSocketFrame(new Date().toString() + ctx.channel().id() + "：" + request);
            // 应答客户端【谁发的发给谁】
            ctx.channel().writeAndFlush(tws);
            // 如果需要对客户端发来的业务消息做处理，就在这里写代码，把上门的应答注释
            discardService.discard(request);
        }
        else {
            // 不接受文本以外的数据帧类型
            ctx.channel()
                    .writeAndFlush(WebSocketCloseStatus.INVALID_MESSAGE_TYPE).addListener(ChannelFutureListener.CLOSE);
        }
    }
    /**
     * 唯一的一次http请求，用于创建websocket
     * */
    private void handleHttpRequest(ChannelHandlerContext ctx,
                                         FullHttpRequest req) {
        //要求Upgrade为websocket，过滤掉get/Post
        if (!req.decoderResult().isSuccess() || (!"websocket".equals(req.headers().get("Upgrade")))) {
            // 若不是websocket方式，则创建BAD_REQUEST的req，返回给客户端
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                "ws://localhost:1024/channel", null, true);
        handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), req);
        }
    }
    /**
     * 拒绝不合法请求，并返回错误信息
     * */
    private void sendHttpResponse(ChannelHandlerContext ctx,FullHttpRequest req, DefaultFullHttpResponse res) {
        // 返回应答给客户端
        if (res.status().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
        }
        ChannelFuture future = ctx.channel().writeAndFlush(res);
        // 如果是非Keep-Alive，关闭连接
        if (!isKeepAlive(req) || res.status().code() != 200) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }
}
