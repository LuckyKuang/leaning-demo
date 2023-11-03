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

package com.luckykuang.udp.util;

import com.luckykuang.udp.client.UdpClient;
import com.luckykuang.udp.vo.SendMsgVO;
import io.netty.buffer.Unpooled;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @author luckykuang
 * @date 2023/11/3 18:29
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UdpClientUtils {

    /**
     * 发送消息
     * @param sendMsgVO
     */
    public static void sendMsg(SendMsgVO sendMsgVO) {
        try {
            // 连接UDP服务端
            InetSocketAddress connect = new InetSocketAddress(sendMsgVO.getIp(), sendMsgVO.getPort());
            log.info("sendMsg channelId:{},ip:{},port:{},data:{}",UdpClient.channel.id().asLongText(),
                    connect.getAddress().getHostAddress(),sendMsgVO.getPort(),sendMsgVO.getData());
            byte[] bytes = sendMsgVO.getData().getBytes(CharsetUtil.UTF_8);
            // 封装UDP消息
            DatagramPacket datagramPacket = new DatagramPacket(Unpooled.copiedBuffer(bytes),connect);
            // 发送UDP消息
            UdpClient.channel.writeAndFlush(datagramPacket).sync();
        } catch (Exception e){
            log.error("sendMsg exception",e);
            throw new RuntimeException("sendMsg exception:" + e.getMessage());
        }
    }
}
