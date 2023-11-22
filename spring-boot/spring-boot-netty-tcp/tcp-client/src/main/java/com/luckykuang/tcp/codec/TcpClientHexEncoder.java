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

package com.luckykuang.tcp.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.apache.commons.codec.binary.Hex;

/**
 * 十六进制编码
 * @author luckykuang
 * @date 2023/11/21 16:13
 */
public class TcpClientHexEncoder extends MessageToByteEncoder<String> {
    @Override
    protected void encode(ChannelHandlerContext ctx, String in, ByteBuf out) throws Exception {
        // 去除空格
        in = in.replaceAll("\\s","");
        // 将十六进制字符串指令转换为字节数组
        byte[] bytes = Hex.decodeHex(in);
        // 写入ByteBuf以发送
        out.writeBytes(bytes);
    }
}
