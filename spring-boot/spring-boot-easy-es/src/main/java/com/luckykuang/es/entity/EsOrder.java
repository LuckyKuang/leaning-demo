package com.luckykuang.es.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.dromara.easyes.annotation.HighLight;
import org.dromara.easyes.annotation.IndexField;
import org.dromara.easyes.annotation.IndexId;
import org.dromara.easyes.annotation.IndexName;
import org.dromara.easyes.annotation.rely.FieldStrategy;
import org.dromara.easyes.annotation.rely.FieldType;
import org.dromara.easyes.annotation.rely.IdType;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author luckykuang
 * @since 2024/9/21 11:39
 */
@Data
@IndexName("es_order")
public class EsOrder implements Serializable {
    @Serial
    private static final long serialVersionUID = 9159475432401871952L;
    /**
     * es主键id
     */
    @IndexId(type = IdType.CUSTOMIZE)
    private Long id;

    /**
     * 订单名称
     * 注意：想要支持中文搜索，需要自行安装es的ik或者pinyin插件
     */
    @IndexField(fieldType = FieldType.TEXT)
    private String orderName;

    /**
     * 订单金额
     */
    @IndexField(fieldType = FieldType.DOUBLE)
    private BigDecimal orderPrice;

    /**
     * 订单状态
     */
    @IndexField(fieldType = FieldType.INTEGER)
    private Integer orderStatus;

    /**
     * 订单时间
     * <pre>
     * 注意：如果是从MySQL数据库直接迁移到es，需要注意时间戳类型
     * 例如：
     *      MySQL[UNIX_TIMESTAMP(DATE_FORMAT(CREATE_TIME, '%Y-%m-%d %H:%i:%s')) * 1000]，类型：Long
     *      ElasticSearch[epoch_millis]，类型：Long
     * </pre>
     */
    @IndexField(fieldType = FieldType.DATE,dateFormat = "epoch_millis")
    private Long orderTime;
}
