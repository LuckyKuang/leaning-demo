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

package com.luckykuang.ping.service.impl;

import com.luckykuang.ping.service.PingIpService;
import com.luckykuang.ping.util.IpUtils;
import com.luckykuang.ping.util.TcpScanUtils;
import com.luckykuang.ping.util.UdpSendUtils;
import com.luckykuang.ping.vo.PingVO;
import com.luckykuang.ping.vo.TcpScanVO;
import com.luckykuang.ping.vo.UdpSendVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author luckykuang
 * @date 2023/9/22 18:48
 */
@Service
public class PingIpServiceImpl implements PingIpService {
    @Override
    public List<PingVO> startTcpScan(TcpScanVO vo) {
        List<PingVO> respVOS = new ArrayList<>();
        if (StringUtils.isBlank(vo.getIpPrefix())){
            // 获取所有网卡的IP地址前缀
            List<String> localAddressList = IpUtils.getLocalAddressPrefixList();
            for (String localAddr : localAddressList) {
                // 设置IP前缀
                vo.setIpPrefix(localAddr);
                startScanAddress(vo, respVOS);
            }
        } else {
            startScanAddress(vo, respVOS);
        }
        return respVOS;
    }

    @Override
    public List<Map<String,String>> startUdpSend(UdpSendVO vo) {
        List<Map<String,String>> respList = new ArrayList<>();
        Integer type = vo.getType();
        if (StringUtils.isBlank(vo.getIpPrefix())){
            // 获取所有网卡的IP地址前缀
            List<String> localAddressList = IpUtils.getLocalAddressPrefixList();
            for (String localAddr : localAddressList) {
                // 设置IP前缀
                vo.setIpPrefix(localAddr);
                respList.add(startSendAddress(vo, type));
            }
        } else {
            respList.add(startSendAddress(vo, type));
        }
        return respList;
    }

    private void startScanAddress(TcpScanVO vo, List<PingVO> respVOS) {
        List<String> ipList = TcpScanUtils.scanAddress(vo.getIpPrefix(), vo.getPort(), vo.getStartAddr(), vo.getEndAddr());
        for (String ip : ipList) {
            PingVO respVO = new PingVO();
            respVO.setIp(ip);
            respVO.setPort(vo.getPort());
            respVOS.add(respVO);
        }
    }

    private Map<String,String> startSendAddress(UdpSendVO vo, Integer type) {
        return switch (type) {
            // 单播
            case 1 -> UdpSendUtils.unicast(vo.getIpPrefix(), vo.getPort(), vo.getData());
            // 广播
            case 2 -> UdpSendUtils.broadcast(vo.getIpPrefix(), vo.getPort(), vo.getData());
            // 组播
            case 3 -> UdpSendUtils.multicast(vo.getIpPrefix(), vo.getPort(), vo.getData());
            default -> null;
        };
    }
}
