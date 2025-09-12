package com.luckykuang.excel.service.impl;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.luckykuang.excel.enums.ExportBusiness;
import com.luckykuang.excel.enums.ExportStatus;
import com.luckykuang.excel.mapper.ExportManagerMapper;
import com.luckykuang.excel.model.dto.ExportManagerDTO;
import com.luckykuang.excel.model.entity.ExportManager;
import com.luckykuang.excel.processor.ExportProcessor;
import com.luckykuang.excel.processor.ExportProcessorFactory;
import com.luckykuang.excel.service.ExportService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * @author luckykuang
 * @since 2025/2/11 11:04
 */
@Slf4j
@Service
@AllArgsConstructor
public class ExportServiceImpl implements ExportService {

    private final ExportProcessorFactory exportProcessorFactory;
    private final ExportManagerMapper exportManagerMapper;

    @Override
    public void exportExcel(ExportManagerDTO<?> exportManagerDTO) {

        ExportBusiness exportBusiness = exportManagerDTO.getExportBusiness();

        ExportManager exportManager = new ExportManager();
        String id = UUID.randomUUID().toString().replace("-", "");
        exportManager.setId(id);
        exportManager.setBusinessCode(exportBusiness.getCode());
        exportManager.setBusinessName(exportBusiness.getName());
        exportManager.setFileName(exportBusiness.getName());
        exportManager.setExcelType(ExcelTypeEnum.XLSX);
        exportManager.setExportStatus(ExportStatus.ING.getCode());
        exportManagerMapper.insert(exportManager);

        ExportManager exportManagerUpdate = new ExportManager();
        exportManagerUpdate.setId(id);
        try {
            ExportProcessor exportProcessor = exportProcessorFactory.getExportProcessor(exportBusiness);
            List<?> list = exportProcessor.process(exportManagerDTO);
            String fileName = this.getClass().getResource("/").getPath() + exportBusiness.getName() + "_" + System.currentTimeMillis() + ".xlsx";
            // 这里 需要指定写用哪个class去读，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
            // 如果这里想使用03 则 传入excelType参数即可
            EasyExcelFactory.write(fileName, exportBusiness.getClazz())
                    .excelType(exportManagerDTO.getExcelType())
                    .sheet(exportBusiness.getName())
                    .doWrite(list);
            exportManagerUpdate.setFilePath(fileName);
            exportManagerUpdate.setExportStatus(ExportStatus.SUCCESS.getCode());
        } catch (Exception e) {
            log.error("导出异常",e);
            exportManagerUpdate.setExportStatus(ExportStatus.FAILURE.getCode());
        }
        exportManagerMapper.updateById(exportManagerUpdate);
    }
}
