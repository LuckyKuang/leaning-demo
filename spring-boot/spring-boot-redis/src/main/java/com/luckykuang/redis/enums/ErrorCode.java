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

package com.luckykuang.redis.enums;

import lombok.Getter;

/**
 * 错误码枚举类
 * @author luckykuang
 * @date 2023/4/10 20:02
 */
@Getter
public enum ErrorCode {
    SUCCESS(200,"操作成功"),
    UNKNOWN(9999,"操作失败"),
    BAD_REQUEST(400,"请求参数不正确"),
    TOKEN_INVALID(401, "Token已过期"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND_REQUEST(404, "请求未找到"),
    METHOD_NOT_ALLOWED_REQUEST(405, "请求方法不正确"),
    BODY_NOT_ALLOWED_REQUEST(406, "请求体不正确"),
    TOKEN_IS_EMPTY(407, "Token为空"),
    TOKEN_ILLEGAL(408, "Token非法"),
    TOKEN_VERIFICATION_FAILED(409, "Token验证失败"),
    TOKEN_INCORRECT(410, "Token不正确"),
    TOKEN_CREATE_EXCEPTION(410, "Token创建异常"),
    FAILED_REQUEST(423, "请求失败，请稍后重试"),
    TOO_MANY_REQUESTS(429, "请求过于频繁，请稍后重试"),
    INTERNAL_SERVER_ERROR(500, "系统异常，请联系管理员"),
    NOT_IMPLEMENTED(501, "功能未实现/未开启"),
    REPEATED_REQUESTS(900, "重复请求，请稍后重试"),
    DEMO_DENY(901, "演示模式，禁止写操作"),
    ;
    private final Integer code;
    private final String message;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
