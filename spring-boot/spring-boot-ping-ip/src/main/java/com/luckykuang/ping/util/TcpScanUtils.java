package com.luckykuang.ping.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * tcp扫描IP网段工具类
 * @author luckykuang
 * @date 2023/9/22 14:29
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TcpScanUtils {

    private static final int THREAD_POOL_SIZE = 20;

    // 线程池的关闭，本工具类不维护，交由业务端自己维护
    public static final ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    /**
     * 扫描网卡
     * @param ipPrefix  IP地址前缀 如：192.168.1
     * @param port      端口号
     * @param startAddr 地址开始位
     * @param endAddr   地址结束位
     * @return          所有的在线机器列表
     */
    public static List<String> scanAddress(String ipPrefix,Integer port,Integer startAddr,Integer endAddr){
        List<Future<List<String>>> futures = new ArrayList<>();
        List<String> resultList = new ArrayList<>();
        // 路由器地址和广播地址没必要扫描，只需要扫描2-254的地址即可
        int start = (startAddr == null || startAddr < 2) ? 2 : startAddr;
        int end = (endAddr == null || endAddr > 254) ? 254 : endAddr;
        if (start > end){
            start = 2;
            end = 254;
        }
        for (int i = start; i <= end; i++) {
            String ipAddress = ipPrefix + "." + i;
            Future<List<String>> future = executor.submit(() -> {
                List<String> addrList = new ArrayList<>();
                boolean bool;
                if (port != null){
                    bool = pingDeviceIpAndPort(ipAddress,port);
                }else {
                    bool = pingDeviceIp(ipAddress);
                }
                if (bool) {
                    addrList.add(ipAddress);
                }
                return addrList;
            });
            futures.add(future);
        }
        for (Future<List<String>> future : futures) {
            try {
                List<String> addrList = future.get();
                resultList.addAll(addrList);
            } catch (InterruptedException e) {
                log.error("scanIp thread InterruptedException",e);
                Thread.currentThread().interrupt();
                throw new RuntimeException("scanIp thread InterruptedException");
            } catch (ExecutionException e) {
                log.error("scanIp thread ExecutionException",e);
                throw new RuntimeException("scanIp thread ExecutionException");
            }
        }
//        executor.shutdown();
        return resultList;
    }

    /**
     * 通过IP寻找机器
     * @param ip IP地址
     * @return   在线-true 离线-false
     */
    public static boolean pingDeviceIp(String ip){
        boolean internalIP = IpUtils.isInternalIP(ip);
        if (!internalIP){
           // 不正确的IP地址
            return false;
        }
        try{
            return InetAddress.getByName(ip).isReachable(1000);
        }catch (Exception e){
            // 机器不可达
            return false;
        }
    }

    /**
     * 通过IP和port寻找机器
     * @param ip    IP地址
     * @param port  端口号
     * @return      在线-true 离线-false
     */
    public static boolean pingDeviceIpAndPort(String ip,int port){
        boolean internalIP = IpUtils.verifyIpAndPort(ip,port);
        if (!internalIP){
            // 不正确的IP地址或端口
            return false;
        }
        try(Socket socket = new Socket();) {
            InetSocketAddress address = new InetSocketAddress(ip, port);
            socket.connect(address, 1000);
        } catch (IOException e) {
            // 端口关闭或机器不可达
            return false;
        }
        return true;
    }
}
