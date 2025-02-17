package com.luckykuang.es.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.dromara.easyes.annotation.HighLight;
import org.dromara.easyes.annotation.IndexField;
import org.dromara.easyes.annotation.IndexName;
import org.dromara.easyes.annotation.rely.Analyzer;
import org.dromara.easyes.annotation.rely.FieldType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 书本信息
 * @author luckykuang
 * @since 2025/2/14 12:17
 */
@Data
@IndexName("es_document")
public class EsDocument {
    /**
     * 推荐方式1: es中的唯一id,不加任何注解或@IndexId(type=IdType.NONE) 此时id值将由es自动生成
     */
    private String id;

    /**
     * 推荐方式3:如果你确实有需求用到其它数据库中的id,不妨在加了推荐方式1中的id后,再加一个字段类型为keyword的列,用来存储其它数据库中的id
     */
    @IndexField(fieldType = FieldType.LONG)
    private Long mysqlId;

    /**
     * 标题
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT, analyzer = Analyzer.IK_MAX_WORD)
    private String title;

    /**
     * 简介内容
     */
    @IndexField(fieldType = FieldType.TEXT, analyzer = Analyzer.IK_MAX_WORD)
    @HighLight(mappingField = "张三", preTag = "<font color='red'>", postTag = "</font>")
    private String content;

    /**
     * 价格
     */
    @IndexField(fieldType = FieldType.DOUBLE)
    private BigDecimal price;

    /**
     * 图书馆占比
     */
    @IndexField(fieldType = FieldType.DOUBLE)
    private Double ratio;

    /**
     * 书架占比
     */
    @IndexField(fieldType = FieldType.FLOAT)
    private Float rate;

    /**
     * 创建时间
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @IndexField(fieldType = FieldType.DATE, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 时间
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @IndexField(fieldType = FieldType.DATE, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date dateTime;

    /**
     * 索引中不存在的字段
     */
    @IndexField(exist = false)
    private String notExistField;

    /**
     * 数量
     */
    @IndexField(fieldType = FieldType.INTEGER)
    private Integer number;
}
