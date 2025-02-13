package com.luckykuang.excel.service.impl;

import com.luckykuang.excel.enums.ImportBusiness;
import com.luckykuang.excel.model.dto.ImportManagerDTO;
import com.luckykuang.excel.processor.ImportProcessor;
import com.luckykuang.excel.processor.ImportProcessorFactory;
import com.luckykuang.excel.service.ImportService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


/**
 * @author luckykuang
 * @since 2025/2/11 15:52
 */
@Slf4j
@Service
@AllArgsConstructor
public class ImportServiceImpl implements ImportService {

    private final ImportProcessorFactory importProcessorFactory;

    @Override
    public String importExcel(String businessCode, MultipartFile file) {
        ImportBusiness importBusiness = ImportBusiness.getByCode(businessCode);
        ImportManagerDTO importManagerDTO = new ImportManagerDTO();
        importManagerDTO.setImportBusiness(importBusiness);
        importManagerDTO.setFile(file);
        ImportProcessor importProcessor = importProcessorFactory.getImportProcessor(importBusiness);
        importProcessor.process(importManagerDTO);
        return "SUCCESS";
    }
}
