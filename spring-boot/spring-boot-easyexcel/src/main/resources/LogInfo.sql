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

CREATE TABLE log_info(
    ID              INT AUTO_INCREMENT COMMENT '日志ID' PRIMARY KEY,
    METHOD_TYPE     VARCHAR(255) NULL COMMENT '方法类型',
    PARAMS          TEXT         NULL COMMENT '入参',
    RESPONSE        TEXT         NULL COMMENT '出参',
    METHOD_NAME     VARCHAR(255) NULL COMMENT '方法名',
    METHOD_DESC     VARCHAR(255) NULL COMMENT '方法描述',
    `STATUS`          VARCHAR(7)   NULL COMMENT '状态 success failure',
    ERROR_DESC      TEXT         NULL COMMENT '错误描述',
    CREATE_USER     VARCHAR(50)  NULL COMMENT '创建人',
    CREATE_TIME     DATETIME     NULL COMMENT '创建时间'
)ENGINE=INNODB DEFAULT CHARSET=UTF8MB4 COMMENT '日志表';