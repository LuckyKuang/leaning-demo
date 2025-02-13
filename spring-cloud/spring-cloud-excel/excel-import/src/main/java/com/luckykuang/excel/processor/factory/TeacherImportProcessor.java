package com.luckykuang.excel.processor.factory;

import com.alibaba.excel.EasyExcelFactory;
import com.luckykuang.excel.aspect.ExcelImport;
import com.luckykuang.excel.enums.ImportBusiness;
import com.luckykuang.excel.enums.ImportStatus;
import com.luckykuang.excel.listener.TeacherImportListener;
import com.luckykuang.excel.mapper.ImportManagerMapper;
import com.luckykuang.excel.model.dto.ImportManagerDTO;
import com.luckykuang.excel.model.entity.ImportManager;
import com.luckykuang.excel.processor.ImportProcessor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;

/**
 * @author luckykuang
 * @since 2025/2/11 17:03
 */
@Slf4j
@Service
@AllArgsConstructor
@ExcelImport(ImportBusiness.TEACHER_INFO)
public class TeacherImportProcessor implements ImportProcessor {

    private final TeacherImportListener teacherImportListener;
    private final ImportManagerMapper importManagerMapper;

    @Override
    public void process(ImportManagerDTO importManagerDTO) {
        ImportManager importManagerUpdate = new ImportManager();
        try {
            long beginTime = System.currentTimeMillis();
            ImportManager importManager = new ImportManager();
            String id = UUID.randomUUID().toString().replace("-", "");
            importManager.setId(id);
            importManager.setBusinessCode(importManagerDTO.getImportBusiness().getCode());
            importManager.setBusinessName(importManagerDTO.getImportBusiness().getName());
            importManager.setFileName(importManagerDTO.getFile().getOriginalFilename());
            importManager.setImportStatus(ImportStatus.ING.getCode());
            importManagerMapper.insert(importManager);

            importManagerUpdate.setId(id);
            // easyexcel的read方法进行数据读取
            EasyExcelFactory.read(importManagerDTO.getFile().getInputStream(), importManagerDTO.getImportBusiness().getClazz(), teacherImportListener)
                    .sheet()
                    .doRead();
            double time = (System.currentTimeMillis() - beginTime) / 1000.00;
            log.info("导入{}文件耗时：{}秒",importManagerDTO.getImportBusiness().getName(), time);
            importManagerUpdate.setImportStatus(ImportStatus.SUCCESS.getCode());
        } catch (IOException e) {
            log.error("导入{}文件异常",importManagerDTO.getImportBusiness().getName(), e);
            importManagerUpdate.setImportStatus(ImportStatus.FAILURE.getCode());
        }
        importManagerMapper.updateById(importManagerUpdate);
    }
}
