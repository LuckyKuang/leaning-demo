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

package com.luckykuang.ping.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * UDP接收消息工具类
 * @author luckykuang
 * @date 2023/9/25 15:02
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UdpReceiveUtils {

    // 开启/停止 UDP 接收服务的开关
    private static boolean FLAG = true;

    // 建立 UDP 的 Socket 服务
    private static DatagramSocket socket;

    public static void startReceive(int port) {
        try {
            socket = new DatagramSocket(port);
            while(FLAG) {
                // 创建数据包
                byte[] buf = new byte[1024];
                DatagramPacket responsePacket = new DatagramPacket(buf, buf.length);

                // 通过数据包对象的方法解析这些数据，例如：地址、端口、数据内容等
                String ip = responsePacket.getAddress().getHostAddress();

                // 使用接收方法将数据存储到数据包中
                try {
                    // 没有设置超时时间，该方法会一直阻塞
                    socket.receive(responsePacket);
                    String responseMessage = new String(responsePacket.getData(), 0, responsePacket.getLength());
                    log.info("监听到的数据 " + ip + ":" + port + ":" + responseMessage);
                } catch (IOException e) {
                    log.error("UDP接收服务异常",e);
                    FLAG = false;
                }
            }
        } catch (SocketException e){
            log.error("UDP接收服务异常",e);
            FLAG = false;
        }
    }

    public static void stopReceive(){
        if (socket != null) {
            socket.close();
        }
        FLAG = false;
    }
}
