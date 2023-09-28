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
import java.net.*;
import java.util.HashMap;
import java.util.Map;

/**
 * UDP广播IP网段工具类
 * @author luckykuang
 * @date 2023/9/22 18:53
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UdpSendUtils {

    // 接收响应的超时时间，单位为毫秒
    private static final int TIMEOUT = 1000;

    /**
     * 单播
     * @param ipPrefix  IP地址前缀 如：192.168.1
     * @param port      端口号
     * @param data      需要发送的数据
     */
    public static Map<String,String> unicast(String ipPrefix, int port, String data){
        return sendRequest(ipPrefix,port,false,data);
    }

    /**
     * 广播
     * @param ipPrefix  IP地址前缀 如：192.168.1
     * @param port      端口号
     * @param data      需要发送的数据
     */
    public static Map<String,String> broadcast(String ipPrefix, int port, String data){
        return sendRequest(ipPrefix,port,true,data);
    }

    /**
     * 组播
     * @param multicastGroup    组播地址 如：224.0.0.1
     * @param port              端口号
     * @param data              需要发送的数据
     */
    public static Map<String,String> multicast(String multicastGroup, int port, String data){
        return sendMulticastRequest(multicastGroup,port,data);
    }

    /**
     * 发送广播/单播
     * @param ipPrefix  IP地址前缀 如：192.168.1
     * @param port      端口号
     * @param broadcast true-广播 false-单播
     * @param data      需要发送的数据
     */
    private static Map<String,String> sendRequest(String ipPrefix, int port, boolean broadcast, String data) {
        String ipAddress = null;
        String responseMessage = null;
        Map<String,String> resultMap = new HashMap<>();
        try (DatagramSocket socket = new DatagramSocket()) {
            // 设置超时响应时间
            socket.setSoTimeout(TIMEOUT);
            // 需要发送的数据
            byte[] requestData = data.getBytes();
            InetAddress destinationAddress;
            // 广播
            if (broadcast) {
                ipAddress = ipPrefix + ".255";
                destinationAddress = InetAddress.getByName(ipAddress);
                socket.setBroadcast(true);
            }
            // 单播
            else {
                ipAddress = ipPrefix + ".1";
                destinationAddress = InetAddress.getByName(ipAddress);
            }
            // 封装广播/单播数据
            DatagramPacket requestPacket = new DatagramPacket(requestData, requestData.length, destinationAddress, port);
            // 发送数据
            socket.send(requestPacket);
            // 封装响应数据
            byte[] responseData = new byte[1024];
            DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length);

            try {
                // 等待响应，超过setSoTimeout()设置的超时时间，抛异常
                socket.receive(responsePacket);
                responseMessage = new String(responsePacket.getData(), 0, responsePacket.getLength());
                log.info("Received response from " + responsePacket.getAddress() + ":" + responsePacket.getPort() + " - " + responseMessage);
            } catch (SocketTimeoutException e) {
                // 超时内没有收到响应
                log.error("Received response timeout");
            }
        } catch (IOException e) {
            log.warn("udp广播/单播异常",e);
        }
        resultMap.put("address",ipAddress);
        resultMap.put("respMsg",responseMessage);
        return resultMap;
    }

    /**
     * 发送组播
     * @param multicastGroup    组播地址 如：224.0.0.1
     * @param port              端口号
     * @param data              需要发送的数据
     */
    private static Map<String,String> sendMulticastRequest(String multicastGroup, int port, String data) {
        String responseMessage = null;
        Map<String,String> resultMap = new HashMap<>();
        try (MulticastSocket socket = new MulticastSocket()) {
            // 设置超时响应时间
            socket.setSoTimeout(TIMEOUT);
            // 需要发送的数据
            byte[] requestData = data.getBytes();
            InetAddress groupAddress = InetAddress.getByName(multicastGroup);
            // 封装组播数据
            DatagramPacket requestPacket = new DatagramPacket(requestData, requestData.length, groupAddress, port);
            // 发送数据
            socket.send(requestPacket);
            // 封装响应数据
            byte[] responseData = new byte[1024];
            DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length);

            try {
                // 等待响应，超过setSoTimeout()设置的超时时间，抛异常
                socket.receive(responsePacket);
                responseMessage = new String(responsePacket.getData(), 0, responsePacket.getLength());
                log.info("Received response from " + responsePacket.getAddress() + ":" + responsePacket.getPort() + " - " + responseMessage);
            } catch (SocketTimeoutException e) {
                // 超时内没有收到响应
                log.warn("Received response timeout");
            }
        } catch (IOException e) {
            log.error("udp组播异常",e);
        }
        resultMap.put("address",multicastGroup);
        resultMap.put("respMsg",responseMessage);
        return resultMap;
    }
}
