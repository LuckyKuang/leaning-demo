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

package com.luckykuang.udp.controller;

import com.luckykuang.udp.util.UdpClientUtils;
import com.luckykuang.udp.vo.SendMsgVO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author luckykuang
 * @date 2023/11/3 18:42
 */
@RestController
@RequestMapping("udp")
public class UdpClientController {
    @PostMapping("sendMsg")
    public String sendMsg(@RequestBody @Validated SendMsgVO sendMsgVO){
        UdpClientUtils.sendMsg(sendMsgVO);
        return "success";
    }
}