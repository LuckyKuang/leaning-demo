package com.luckykuang.excel.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.luckykuang.excel.model.base.BaseVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 导入管理
 * @author luckykuang
 * @since 2025/2/11 15:55
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ImportManager extends BaseVO {
    /**
     * 主键id
     */
    @TableId(type = IdType.INPUT)
    private String id;
    /**
     * 业务编码
     */
    private String businessCode;
    /**
     * 业务名称
     */
    private String businessName;
    /**
     * 文件名称
     */
    private String fileName;
    /**
     * 导入状态 0-失败 1-成功 2-进行中
     */
    private Integer importStatus;
}
