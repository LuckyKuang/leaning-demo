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

package com.luckykuang.order.rocketmq.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author luckykuang
 * @date 2023/7/31 16:28
 */
@Data
public class UserVo implements Serializable {
    @Serial
    private static final long serialVersionUID = 8026843167296094758L;
    private Long id;
    private String name;

    public static UserVo getUser() {
        UserVo userVo = new UserVo();
        userVo.setId(1L);
        userVo.setName("张三");
        return userVo;
    }
}
