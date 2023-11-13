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

package com.luckykuang.thread.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author luckykuang
 * @date 2023/11/13 10:35
 */
@Data
@TableName("log_info")
public class LogInfo {

    /**
     * 日志id
     */
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    /**
     * 方法类型
     */
    private String methodType;
    /**
     * 入参
     */
    private String params;
    /**
     * 出参
     */
    private String response;
    /**
     * 方法名
     */
    private String methodName;
    /**
     * 方法描述
     */
    private String methodDesc;
    /**
     * 状态 success failure
     */
    private String status;
    /**
     * 错误描述
     */
    private String errorDesc;
    /**
     * 创建人
     */
    private String createUser;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
