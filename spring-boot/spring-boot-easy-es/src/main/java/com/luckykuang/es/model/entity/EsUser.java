package com.luckykuang.es.model.entity;

import lombok.Data;
import org.dromara.easyes.annotation.*;
import org.dromara.easyes.annotation.rely.Analyzer;
import org.dromara.easyes.annotation.rely.FieldStrategy;
import org.dromara.easyes.annotation.rely.FieldType;
import org.dromara.easyes.annotation.rely.IdType;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @author luckykuang
 * @since 2024/8/28 9:52
 */
@Data
@IndexName("es_user")
public class EsUser implements Serializable {
    @Serial
    private static final long serialVersionUID = 4117205210411466607L;

    /**
     * 主键id 默认为 _id
     */
    @IndexId(type = IdType.CUSTOMIZE)
    private Integer id;

    /**
     * 名称
     */
    @IndexField(strategy = FieldStrategy.NOT_EMPTY, fieldType = FieldType.TEXT, analyzer = Analyzer.IK_MAX_WORD)
    // 需要被高亮的字段
    @HighLight(mappingField = "nameHighlightContent", preTag = "<font color='red'>", postTag = "</font>")
    private String name;

    /**
     * 别名
     */
    @IndexField(strategy = FieldStrategy.NOT_EMPTY,fieldType = FieldType.TEXT, analyzer = Analyzer.IK_MAX_WORD)
    private String nickName;

    /**
     * 年龄
     */
    @IndexField(fieldType = FieldType.INTEGER)
    private Integer age;

    /**
     * 性别
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String sex;

    @Score
    private Float score;

    @IndexField(fieldType = FieldType.DATE)
    private Date birthday;

    // 不存在
    @IndexField(exist = false)
    private String description;

    // 不存在
    @IndexField(exist = false)
    private Integer maxAge;

    @IndexField(exist = false)
    private Integer minAge;

    @IndexField(exist = false)
    private String nameHighlightContent;
}
