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

package com.luckykuang.rabbitmq.exception;

import com.luckykuang.rabbitmq.enums.ErrorCodeEnum;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

/**
 * 系统类异常
 * @author luckykuang
 * @date 2023/9/8 14:18
 */
@Getter
@Setter
public class SystemException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -7623032974865338665L;

    /**
     * 错误码
     */
    private final ErrorCodeEnum errorCode;

    public SystemException(ErrorCodeEnum errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
