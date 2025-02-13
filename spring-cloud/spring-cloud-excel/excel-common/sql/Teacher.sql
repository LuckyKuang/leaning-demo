-- auto Generated on 2025-02-12
-- DROP TABLE IF EXISTS teacher;
CREATE TABLE teacher
(
    id          BIGINT(19)  NOT NULL AUTO_INCREMENT COMMENT 'id',
    `name`      VARCHAR(50) NOT NULL DEFAULT '' COMMENT 'name',
    age         INT(11)     NOT NULL DEFAULT -1 COMMENT 'age',
    create_by   BIGINT(19)  NOT NULL DEFAULT -1 COMMENT '创建人',
    update_by   BIGINT(19)  NOT NULL DEFAULT -1 COMMENT '修改人',
    create_time DATETIME    NOT NULL DEFAULT '1000-01-01 00:00:00' COMMENT '创建时间',
    update_time DATETIME    NOT NULL DEFAULT '1000-01-01 00:00:00' COMMENT '修改时间',
    del_flag    TINYINT(3)  NOT NULL DEFAULT 0 COMMENT '逻辑删除 0未删除 1已删除',
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT '教师信息';
