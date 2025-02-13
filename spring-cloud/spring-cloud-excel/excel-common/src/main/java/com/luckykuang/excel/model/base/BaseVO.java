package com.luckykuang.excel.model.base;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author luckykuang
 * @since 2025/2/11 15:00
 */
@Data
public class BaseVO {
    /**
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;
    /**
     * 修改人
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    /**
     * 逻辑删除 0未删除 1已删除
     */
    private Integer delFlag;
}
