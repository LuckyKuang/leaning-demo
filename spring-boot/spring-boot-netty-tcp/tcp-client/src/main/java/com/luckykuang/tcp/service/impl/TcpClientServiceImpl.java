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

package com.luckykuang.tcp.service.impl;

import com.luckykuang.tcp.client.NettyTcpClient;
import com.luckykuang.tcp.service.TcpClientService;
import com.luckykuang.tcp.vo.ConnectVO;
import com.luckykuang.tcp.vo.SendVO;
import io.netty.channel.ChannelFuture;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static com.luckykuang.tcp.constant.Constants.*;

/**
 * @author luckykuang
 * @date 2023/11/21 16:01
 */
@Slf4j
@Service
public class TcpClientServiceImpl implements TcpClientService {

    @Resource
    private NettyTcpClient client;

    /**
     * 注意：服务端只有连接成功后，才能发送信息
     * @param vo
     * @return
     */
    @Override
    public ResponseEntity<String> connect(ConnectVO vo) {
        try {
            new Thread(() -> client.open(vo.getIp(),vo.getPort(),vo.getCodec())).start();
            // 此处应该等服务端连接成功后，才返回连接成功，否则连接失败
            // 建议在channelActive处改用websocket通知到前端，便于消息通知的及时性
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (Exception e){
            log.error("连接服务器失败",e);
            return ResponseEntity.internalServerError().body("连接服务器失败");
        }
        return ResponseEntity.ok("success");
    }

    /**
     * 注意：如果服务端响应消息修改成websocket交互，关闭管道操作也要放在放在消息推送之后
     * @param vo
     * @return
     */
    @Override
    public ResponseEntity<String> send(SendVO vo) {
        String response = null;
        try {
            String sendCache = vo.getIp() + ":" + vo.getPort() + "=" + vo.getCodec();
            ChannelFuture channelFuture = TCP_CACHE_CHANNEL.get(sendCache);
            if (channelFuture != null && channelFuture.channel().isActive()) {
                // 判断是否添加回车换行符号
                String sendData = Boolean.TRUE.equals(vo.getEnter()) ? vo.getData() + "\r\n" : vo.getData();
                // 发送消息
                channelFuture.channel().writeAndFlush(sendData);
                // 写入缓存，用于在channelRead0获取消息是一对一
                TCP_SEND_CACHE.put(channelFuture.channel().id().asLongText(),sendCache);
                // 等待服务端返回消息
                // 建议直接在channelRead0处修改成websocket交互
                TimeUnit.SECONDS.sleep(1);
                // 读取服务器响应的消息
                response = TCP_RESPONSE_DATA.get(channelFuture.channel().id().asLongText());
                // 关闭管道，一次性连接
                // 好处：不影响其他客户端连接服务器
                channelFuture.channel().close();
            }
        } catch (Exception e){
            log.error("发送信息异常",e);
            response = "发送异常，请检查服务器是否可连接!";
        }
        return ResponseEntity.ok(response);
    }
}
