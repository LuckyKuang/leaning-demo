-- auto Generated on 2025-02-12
-- DROP TABLE IF EXISTS import_manager;
CREATE TABLE import_manager
(
    id            VARCHAR(32)  NOT NULL COMMENT '主键id',
    business_code VARCHAR(4)   NOT NULL DEFAULT '' COMMENT '业务编码',
    business_name VARCHAR(256) NOT NULL DEFAULT '' COMMENT '业务名称',
    file_name     VARCHAR(512) NOT NULL DEFAULT '' COMMENT '文件名称',
    import_status TINYINT(4)   NOT NULL DEFAULT 2 COMMENT '导入状态 0-失败 1-成功 2-进行中',
    create_by     BIGINT(19)   NOT NULL DEFAULT -1 COMMENT '创建人',
    update_by     BIGINT(19)   NOT NULL DEFAULT -1 COMMENT '修改人',
    create_time   DATETIME     NOT NULL DEFAULT '1000-01-01 00:00:00' COMMENT '创建时间',
    update_time   DATETIME     NOT NULL DEFAULT '1000-01-01 00:00:00' COMMENT '修改时间',
    del_flag      TINYINT(3)   NOT NULL DEFAULT 0 COMMENT '逻辑删除 0未删除 1已删除',
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT '导入管理';
