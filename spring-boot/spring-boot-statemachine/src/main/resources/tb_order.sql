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

-- auto Generated on 2023-10-09
-- DROP TABLE IF EXISTS tb_order;
CREATE TABLE `tb_order` (
    `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `order_code` varchar(128) DEFAULT NULL COMMENT '订单编码',
    `status` smallint(3) DEFAULT NULL COMMENT '订单状态',
    `name` varchar(64) DEFAULT NULL COMMENT '订单名称',
    `price` decimal(12,2) DEFAULT NULL COMMENT '价格',
    `delete_flag` tinyint(2) NOT NULL DEFAULT '0' COMMENT '删除标记，0未删除  1已删除',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    `create_user` int(11) DEFAULT NULL COMMENT '创建人',
    `update_user` int(11) DEFAULT NULL COMMENT '更新人',
    `version` int(11) NOT NULL DEFAULT '0' COMMENT '版本号',
    `remark` varchar(64) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

insert  into `tb_order`(`id`,`order_code`,`status`,`name`,`price`,`delete_flag`,`create_time`,`update_time`,`create_user`,`update_user`,`version`,`remark`) values (1,'A111',1,'A','22.00',0,'2022-10-15 16:14:11','2022-10-02 21:29:14',1,1,0,NULL);
insert  into `tb_order`(`id`,`order_code`,`status`,`name`,`price`,`delete_flag`,`create_time`,`update_time`,`create_user`,`update_user`,`version`,`remark`) values (2,'A111',1,'订单A','22.00',0,'2022-10-02 21:53:13','2022-10-02 21:29:14',1,1,0,NULL);
insert  into `tb_order`(`id`,`order_code`,`status`,`name`,`price`,`delete_flag`,`create_time`,`update_time`,`create_user`,`update_user`,`version`,`remark`) values (3,'A111',1,'订单A','22.00',0,'2022-10-02 21:53:13','2022-10-02 21:29:14',1,1,0,NULL);
insert  into `tb_order`(`id`,`order_code`,`status`,`name`,`price`,`delete_flag`,`create_time`,`update_time`,`create_user`,`update_user`,`version`,`remark`) values (4,'A111',1,'订单A','22.00',0,'2022-10-03 09:08:30','2022-10-02 21:29:14',1,1,0,NULL);