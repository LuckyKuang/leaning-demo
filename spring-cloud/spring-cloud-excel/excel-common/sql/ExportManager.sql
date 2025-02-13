-- auto Generated on 2025-02-12
-- DROP TABLE IF EXISTS export_manager;
CREATE TABLE export_manager
(
    id            VARCHAR(32)  NOT NULL COMMENT '主键id',
    business_code VARCHAR(4)   NOT NULL DEFAULT '' COMMENT '业务编码',
    business_name VARCHAR(50)  NOT NULL DEFAULT '' COMMENT '业务名称',
    file_name     VARCHAR(256) NOT NULL DEFAULT '' COMMENT '文件名称',
    file_path     VARCHAR(512) NOT NULL DEFAULT '' COMMENT '文件路径',
    excel_type    VARCHAR(4)   NOT NULL DEFAULT '' COMMENT '导出类型 XLSX XLX CSV',
    export_status INT(11)      NOT NULL DEFAULT -1 COMMENT '导出状态 0-失败 1-成功 2-进行中',
    create_by     BIGINT(19)   NOT NULL DEFAULT -1 COMMENT '创建人',
    update_by     BIGINT(19)   NOT NULL DEFAULT -1 COMMENT '修改人',
    create_time   DATETIME     NOT NULL DEFAULT '1000-01-01 00:00:00' COMMENT '创建时间',
    update_time   DATETIME     NOT NULL DEFAULT '1000-01-01 00:00:00' COMMENT '修改时间',
    del_flag      TINYINT(3)   NOT NULL DEFAULT 0 COMMENT '逻辑删除 0未删除 1已删除',
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT '导出管理';
