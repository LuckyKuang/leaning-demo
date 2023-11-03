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

package com.luckykuang.udp.server;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author luckykuang
 * @date 2023/11/3 15:48
 */
@Slf4j
public class UdpServerReceivedHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
        String receivedData = packet.content().toString(CharsetUtil.UTF_8);
        try {
            // 此处收到客户端发送过来的消息，对不同的消息做不同的业务回应
            log.info("received ip:{},port:{},data:{}",packet.sender().getAddress().getHostAddress(),
                    packet.sender().getPort(),receivedData);
            byte[] bytes = "ok".getBytes(CharsetUtil.UTF_8);
            ctx.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(bytes), packet.sender()));
        } catch (Exception e) {
            byte[] bytes = "fail".getBytes(CharsetUtil.UTF_8);
            ctx.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(bytes), packet.sender()));
            log.error("received exception data:[{}]",receivedData,e);
        }
    }
}
