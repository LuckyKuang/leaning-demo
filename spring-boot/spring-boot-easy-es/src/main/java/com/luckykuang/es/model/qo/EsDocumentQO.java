package com.luckykuang.es.model.qo;

import lombok.Data;

/**
 * 书本信息
 * @author luckykuang
 * @since 2025/2/14 12:17
 */
@Data
public class EsDocumentQO {
    /**
     * esId
     */
    private String id;
    /**
     * mysqlId
     */
    private Long mysqlId;
    /**
     * 标题
     */
    private String title;
    /**
     * 简介内容
     */
    private String content;
    /**
     * 开始时间
     */
    private String startTime;
    /**
     * 结束时间
     */
    private String endTime;
}
