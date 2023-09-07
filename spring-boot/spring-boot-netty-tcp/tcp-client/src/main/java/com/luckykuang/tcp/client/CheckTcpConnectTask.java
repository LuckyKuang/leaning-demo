package com.luckykuang.tcp.client;
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

import com.luckykuang.tcp.util.TcpClientUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author luckykuang
 * @date 2023/8/24 17:00
 */
@Slf4j
@Component
@EnableScheduling
public class CheckTcpConnectTask {

    @Resource
    private NettyTcpClient nettyTcpClient;
    /**
     * 连接状态获取，一定要写在全局，写在定时任务方法中，获取的值会有问题
     */
    private static final Optional<Boolean> tcpConnected = TcpClientUtils.getTcpConnected();

    @Scheduled(cron = "0/10 * * * * ? ")
    public void checkReconnectTcpServer(){
        log.debug("发起重连检测");
        if(tcpConnected.isEmpty() || Boolean.FALSE.equals(tcpConnected.get())){
            log.info("tcp client执行重连中-----------------");
            nettyTcpClient.connect();
        }
    }
}
