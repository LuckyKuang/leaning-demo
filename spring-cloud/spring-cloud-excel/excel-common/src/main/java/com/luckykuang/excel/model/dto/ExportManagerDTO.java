package com.luckykuang.excel.model.dto;

import com.alibaba.excel.support.ExcelTypeEnum;
import com.luckykuang.excel.enums.ExportBusiness;
import lombok.Data;

/**
 * excel导出父类
 * @author luckykuang
 * @since 2025/2/11 10:29
 */
@Data
public class ExportManagerDTO<T> {
    /**
     * 业务类型
     */
    private ExportBusiness exportBusiness;
    /**
     * 导出类型
     */
    private ExcelTypeEnum excelType = ExcelTypeEnum.XLSX;
    /**
     * 查询参数
     */
    private T query;
}
