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

import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * 网卡工具类
 * @author luckykuang
 * @date 2023/12/14 18:33
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NetworkUtils {

    public static void main(String[] args) {
        System.out.println(getLocalAddressList());
        System.out.println(getLocalBroadcastAddressList());
        System.out.println(getLocalMulticastAddressList());
    }

    public static List<String> getLocalBroadcastAddressList2(){
        List<String> resultAddress = new ArrayList<>();
        try {
            // 获得本机的所有网络接口
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                if (networkInterface.isLoopback()){
                    // 过滤回环地址
                    continue;
                }
                // 获得与该网络接口绑定的 IP 地址，一般只有一个
                List<InterfaceAddress> interfaceAddresses = networkInterface.getInterfaceAddresses();
                for (InterfaceAddress interfaceAddress : interfaceAddresses) {
                    InetAddress address = interfaceAddress.getBroadcast();
                    if (address instanceof Inet4Address inet4Broadcast) {
                        // 只关心 IPv4 地址
                        resultAddress.add(inet4Broadcast.getHostAddress());
                    }
                }
            }
        } catch (Exception e){
            log.error("getLocalBroadcastAddressList exception",e);
        }
        return resultAddress;
    }

    /**
     * 获取本机所有网卡的IP地址
     */
    public static List<InetAddress> getLocalAddressList(){
        List<InetAddress> resultAddress = new ArrayList<>();
        try {
            // 获得本机的所有网络接口
            Enumeration<NetworkInterface> nifs = NetworkInterface.getNetworkInterfaces();
            while (nifs.hasMoreElements()) {
                NetworkInterface nif = nifs.nextElement();
                // 过滤回环地址
                if(nif.isLoopback()||!nif.isUp()||!nif.supportsMulticast()){
                    continue;
                }
                // 获得与该网络接口绑定的 IP 地址，一般只有一个
                Enumeration<InetAddress> addresses = nif.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (address instanceof Inet4Address) {
                        // 只关心 IPv4 地址
                        resultAddress.add(address);
                    }
                }
            }
        } catch (Exception e){
            log.error("获取本地IP地址异常",e);
        }
        return resultAddress;
    }

    /**
     * 获取本机所有网卡的广播地址
     */
    public static List<InetAddress> getLocalBroadcastAddressList() {
        List<InetAddress> addrList = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                List<InterfaceAddress> interfaceAddresses = networkInterface.getInterfaceAddresses();
                for (InterfaceAddress interfaceAddress : interfaceAddresses) {
                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    if (broadcast instanceof Inet4Address){
                        if ("127.255.255.255".equals(broadcast.getHostAddress())){
                            continue;
                        }
                        addrList.add(broadcast);
                    }
                }
            }
        } catch (Exception e){
            log.error("获取本地广播地址异常：",e);
        }
        return addrList;
    }

    /**
     * 获取组播地址
     */
    public static List<InetAddress> getLocalMulticastAddressList() {
        List<InetAddress> addrList = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (address.isMulticastAddress()) {
                        addrList.add(address);
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return addrList;
    }
}
