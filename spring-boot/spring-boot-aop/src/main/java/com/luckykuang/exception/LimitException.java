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

package com.luckykuang.exception;

import lombok.Getter;

import java.io.Serial;

/**
 * @author luckykuang
 * @date 2024/4/15 16:35
 */
@Getter
public class LimitException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 8462600857416517545L;
    private final String msg;

    public LimitException(String msg) {
        super();
        this.msg = msg;
    }
}
