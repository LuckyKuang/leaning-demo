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

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;

/**
 * ping扫描IP工具类
 * @author luckykuang
 * @date 2023/9/20 16:58
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class IpUtils {

    private static final String IP_REGEX =
            "^((\\d{1,2}|1\\d{2}|2[0-4]\\d|25[0-5])\\.){3}(\\d{1,2}|1\\d{2}|2[0-4]\\d|25[0-5])$";

    /**
     * 获取所有的IP地址前缀 如：192.168.1
     */
    public static List<String> getLocalAddressPrefixList(){
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
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (address instanceof Inet4Address inet4Address) {
                        // 只关心 IPv4 地址
                        String hostAddress = inet4Address.getHostAddress();
                        String resultAddr = hostAddress.substring(0, hostAddress.lastIndexOf("."));
                        resultAddress.add(resultAddr);
                    }
                }
            }
        } catch (Exception e){
            log.error("getLocalAddressList exception",e);
        }
        return resultAddress;
    }

    /**
     * 检查IP是否属于指定IP段
     * @param ipAddress IP地址
     * @return          true-是 false-否
     */
    public static boolean isInternalIP(String ipAddress) {
        boolean isValid = isValidIP(ipAddress);
        if (!isValid){
            return false;
        }
        long ip = ipToLong(ipAddress);

        // 检查IP是否属于内网IP段
        return isInRange(ip, ipToLong("10.0.0.0"), ipToLong("10.255.255.255")) ||
                isInRange(ip, ipToLong("192.168.0.0"), ipToLong("192.168.255.255")) ||
                isInRange(ip, ipToLong("172.16.0.0"), ipToLong("172.31.255.255"));
    }

    /**
     * 校验IP和端口是否在合法范围内
     * @param ip    IP地址
     * @param port  端口号
     * @return      true-合法 false-非法
     */
    public static boolean verifyIpAndPort(String ip, int port) {
        return null != ip && isInternalIP(ip) && isRangeInt(port);
    }

    /**
     * 校验IP地址是否输入合法
     * @param ipAddress IP地址
     * @return          true-合法 false-非法
     */
    private static boolean isValidIP(String ipAddress) {
        return Pattern.matches(IP_REGEX, ipAddress);
    }

    /**
     * 将IP转换成Long型
     * @param ipAddress IP地址
     * @return Long型的IP
     */
    private static long ipToLong(String ipAddress) {
        String[] ipParts = ipAddress.split("\\.");
        long result = 0;
        for (int i = 0; i < 4; i++) {
            result += Long.parseLong(ipParts[i]) << (24 - (8 * i));
        }
        return result;
    }

    /**
     * 校验IP是否在合法范围内
     * @param ip        Long型的IP地址
     * @param startIp   指定的起始IP
     * @param endIp     指定的结束IP
     * @return          true-合法 false-非法
     */
    private static boolean isInRange(long ip, long startIp, long endIp) {
        return ip >= startIp && ip <= endIp;
    }

    /**
     * 校验端口是否在合法范围内
     *
     * @param port 端口号
     * @return true-合法 false-非法
     */
    private static boolean isRangeInt(int port) {
        return port > 1024 && port < 65535;
    }
}
