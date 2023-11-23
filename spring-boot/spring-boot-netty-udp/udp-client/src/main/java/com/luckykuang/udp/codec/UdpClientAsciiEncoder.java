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

import com.luckykuang.udp.vo.SendMsgVO;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * 十进制编码
 * @author luckykuang
 * @date 2023/11/22 17:21
 */
@Slf4j
public class UdpClientAsciiEncoder extends MessageToMessageEncoder<SendMsgVO> {

    @Override
    protected void encode(ChannelHandlerContext ctx, SendMsgVO in, List<Object> out) throws Exception {
        InetSocketAddress inetSocketAddress = in.getInetSocketAddress();
        String data = in.getData();
        byte[] bytes = data.getBytes(CharsetUtil.US_ASCII);
        // 封装UDP消息
        DatagramPacket datagramPacket = new DatagramPacket(Unpooled.copiedBuffer(bytes),inetSocketAddress);
        log.info("send udp ascii ip:[{}],port:[{}],data:[{}]", inetSocketAddress.getAddress().getHostAddress(),
                inetSocketAddress.getPort(),data);
        out.add(datagramPacket);
    }
}
