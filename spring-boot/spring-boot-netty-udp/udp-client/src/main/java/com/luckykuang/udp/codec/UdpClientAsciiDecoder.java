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

package com.luckykuang.udp.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static io.netty.util.CharsetUtil.UTF_8;

/**
 * 十进制解码
 * @author luckykuang
 * @date 2023/11/22 17:21
 */
@Slf4j
public class UdpClientAsciiDecoder extends MessageToMessageDecoder<DatagramPacket> {

    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket in, List<Object> out) throws Exception {
        String ip = in.sender().getAddress().getHostAddress();
        int port = in.sender().getPort();
        ByteBuf byteBuf = in.content();
        int readableBytes = byteBuf.readableBytes();
        String received = null;
        if (readableBytes > 0){
            byte[] bytes = new byte[readableBytes];
            byteBuf.readBytes(bytes);
            received = new String(bytes,UTF_8);
            out.add(received);
        }
        log.info("received udp ascii ip:[{}],port:[{}],data:[{}]", ip,port,received);
    }
}
