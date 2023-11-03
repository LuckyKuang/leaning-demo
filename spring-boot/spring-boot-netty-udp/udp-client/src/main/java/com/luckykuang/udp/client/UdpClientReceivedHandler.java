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

package com.luckykuang.udp.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author luckykuang
 * @date 2023/11/3 17:31
 */
@Slf4j
public class UdpClientReceivedHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
        String receivedData = packet.content().toString(CharsetUtil.UTF_8);
        try {
            // 此处收到服务端返回的消息后，对数据进行业务处理
            log.info("received channelId:{} ip:{},port:{},data:{}",ctx.channel().id().asLongText(),
                    packet.sender().getAddress().getHostAddress(),packet.sender().getPort(),receivedData);
        } catch (Exception e) {
            log.error("received exception data:[{}]",receivedData,e);
        }
    }
}
