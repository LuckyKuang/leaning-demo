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

package com.luckykuang.udp.service.impl;

import com.luckykuang.udp.client.UdpClient;
import com.luckykuang.udp.client.UdpClientChannelInitializer;
import com.luckykuang.udp.service.UdpClientService;
import com.luckykuang.udp.vo.SendMsgVO;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;

/**
 * @author luckykuang
 * @date 2023/11/23 16:24
 */
@Slf4j
@Service
public class UdpClientServiceImpl implements UdpClientService {
    @Override
    public String send(String ip, Integer port, String data,String codec) {
        // 设置编码
        UdpClientChannelInitializer.setUdpCodec(codec);
        // 封装地址
        InetSocketAddress inetSocketAddress = new InetSocketAddress(ip,port);
        SendMsgVO vo = new SendMsgVO();
        vo.setData(data);
        vo.setInetSocketAddress(inetSocketAddress);
        Channel channel = UdpClient.open();
        channel.writeAndFlush(vo);
        return "success";
    }
}
