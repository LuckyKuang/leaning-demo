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

package com.luckykuang.ping.controller;

import com.luckykuang.ping.service.PingIpService;
import com.luckykuang.ping.vo.PingVO;
import com.luckykuang.ping.vo.TcpScanVO;
import com.luckykuang.ping.vo.UdpSendVO;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author luckykuang
 * @date 2023/9/22 18:47
 */
@RestController
@RequestMapping("ping")
@RequiredArgsConstructor
public class PingIpController {

    private final PingIpService pingIpService;

    /**
     * 开始TCP扫描
     * 本接口响应可能会需要几十秒，具体看机器性能
     * 如果是前后端分离方案，可以采取异步返回的形式，扫描到了地址就通过websocket推动给前端
     * @param vo 可以指定IP地址区间搜索，也可以指定端口号搜索，默认搜索所有本机网卡的区间
     * @return 在线机器列表
     */
    @PostMapping("startTcpScan")
    public List<PingVO> startTcpScan(@RequestBody TcpScanVO vo){
        return pingIpService.startTcpScan(vo);
    }

    /**
     * 开始UDP广播
     * @param vo 可以指定IP地址区间广播，端口号必填 默认广播所有本机网卡
     * @return 广播的地址和回调消息 超时没有回调则为空
     */
    @PostMapping("startUdpSend")
    public List<Map<String,String>> startUdpSend(@RequestBody @Validated UdpSendVO vo){
        return pingIpService.startUdpSend(vo);
    }
}
