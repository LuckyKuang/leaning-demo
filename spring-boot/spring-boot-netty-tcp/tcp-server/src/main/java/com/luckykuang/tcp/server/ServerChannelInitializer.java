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

import com.luckykuang.tcp.codec.TcpServerAsciiDecoder;
import com.luckykuang.tcp.codec.TcpServerAsciiEncoder;
import com.luckykuang.tcp.codec.TcpServerHexDecoder;
import com.luckykuang.tcp.codec.TcpServerHexEncoder;
import com.luckykuang.tcp.config.TcpServerConfig;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 初始化通道
 * @author luckykuang
 * @date 2023/8/23 14:55
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Resource
    private TcpServerConfig config;

    /**
     * ChannelPipeline管道底层是责任链模式，所以特别是编码解码部分顺序一定不要搞错
     * 服务端需要什么编码/解码，就用哪个编码/解码
     * Netty解决TCP粘包/拆包(在没有自定义编解码的情况下，配合StringDecoder即可直接接收字符串)
     *      1.按行文本解码器LineBasedFramedDecoder和StringDecoder
     *          LineBasedFramedDecoder：依次遍历ByeBuf中可读字节，判断是否有“\n”，“\r\n”，如果有，就当前位置为结束位置，从可读索引到结束位置区间的字节就组装成一行，以换行符为结束标志的解码器，同识支持最大长度。
     *          StringDecoder：将接收对象转换成字符串，然后继续调用后面的handler
     *      2.按分隔符文本解码器DelimiterBasedFrameDecoder和StringDecoder
     *      3.固定长度解码器FixedLengthFrameDecoder和StringDecoder
     * 注意事项：
     *      行文本解码器设置maxLength后
     *          1.单次请求文本必须小于这个长度，否则抛出异常
     *          2.如果单次请求中没有添加“\n”或“\r\n”符号，netty会默认缓存下来，直到收到“\n”或“\r\n”符号才会清空缓存，如果累计缓存长度超过maxLength，将会抛出异常
     *      分隔符文本解码器设置maxLength后
     *          1.单次请求文本必须小于这个长度，否则抛出异常
     *          2.如果单次请求中没有添加分割符号，netty会默认缓存下来，直到收到分割符号才会清空缓存，如果累计缓存长度超过maxLength，将会抛出异常
     *      固定长度解码器设置frameLength后
     *          每次请求必须强制等于这个长度，不够用空格填补，如果小于这个长度，netty会缓存下来，直到长度达到固定长度才输出数据，多余的数据将会缓存用于下次请求累加，最终会造成消息被切割
     */
    @Override
    protected void initChannel(SocketChannel socketChannel) {
        ChannelPipeline pipeline = socketChannel.pipeline();
        // 心跳机制,超时会触发Handle中userEventTrigger()方法
        pipeline.addLast(new IdleStateHandler(config.getReaderIdleTime(),config.getWriterIdleTime(),
                config.getAllIdleTime(),TimeUnit.MINUTES));
        String codec = config.getCodec();
        // 字符串编解码器
        if ("ascii".equalsIgnoreCase(codec)) {
            // 十进制编码/解码
            pipeline.addLast(new TcpServerAsciiEncoder());
            // 行文本解码器
            pipeline.addLast(new LineBasedFrameDecoder(1024), new TcpServerAsciiDecoder());
            // 分隔符文本解码器，已"$_"符号分割，后续返回客户端消息也需要追加分隔符
//            ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());
//            pipeline.addLast(new DelimiterBasedFrameDecoder(1024, delimiter), new TcpServerAsciiDecoder());
            // 固定长度解码器
//            pipeline.addLast(new FixedLengthFrameDecoder(16), new TcpServerAsciiDecoder());
        } else if ("hex".equalsIgnoreCase(codec)) {
            // 十六进制编码/解码
            pipeline.addLast(new TcpServerHexEncoder(), new TcpServerHexDecoder());
        } else {
            log.warn("编码为空或不支持的编码：{}，请检查配置",codec);
            throw new RuntimeException("编码为空或不支持的编码，请检查配置");
        }

        // 自定义Handler
        pipeline.addLast(new ServerChannelInboundHandler());
    }
}
