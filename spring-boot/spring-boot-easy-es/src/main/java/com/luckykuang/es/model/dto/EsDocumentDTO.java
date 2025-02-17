package com.luckykuang.es.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.dromara.easyes.core.conditions.function.Update;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 书本信息
 * @author luckykuang
 * @since 2025/2/14 12:17
 */
@Data
public class EsDocumentDTO {
    /**
     * esId
     */
    @NotBlank(groups = {Update.class})
    private String id;

    /**
     * mysqlId
     */
    @NotNull
    private Long mysqlId;

    /**
     * 标题
     */
    @NotBlank
    private String title;

    /**
     * 简介内容
     */
    @NotBlank
    private String content;

    /**
     * 价格
     */
    @NotNull
    private BigDecimal price;

    /**
     * 图书馆占比
     */
    @NotNull
    private Double ratio;

    /**
     * 书架占比
     */
    @NotNull
    private Float rate;

    /**
     * 创建时间
     */
    @NotNull
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 时间
     */
    @NotNull
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dateTime;

    /**
     * 数量
     */
    @NotNull
    private Integer number;
}
