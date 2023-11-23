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
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 十六进制解码
 * @author luckykuang
 * @date 2023/11/22 17:21
 */
@Slf4j
public class UdpClientHexDecoder extends MessageToMessageDecoder<DatagramPacket> {
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
            received = Hex.encodeHexString(bytes);
            received = addSpaceEveryTwo(received);
            out.add(received);
        }
        log.info("received udp hex ip:[{}],port:[{}],data:[{}]", ip,port,received);
    }

    /**
     * 十六进制的数据每两个字符添加空格
     */
    private static String addSpaceEveryTwo(String input) {
        if (StringUtils.isBlank(input)){
            return null;
        }
        input = input.toUpperCase();
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < input.length(); i += 2) {
            output.append(input, i, i + 2).append(" ");
        }
        return output.toString().trim();
    }
}
