package com.luckykuang.excel.service.impl;

import com.luckykuang.excel.enums.ExportBusiness;
import com.luckykuang.excel.enums.ImportBusiness;
import com.luckykuang.excel.feign.ExportClient;
import com.luckykuang.excel.feign.ImportClient;
import com.luckykuang.excel.model.dto.ExportManagerDTO;
import com.luckykuang.excel.model.qo.StudentQO;
import com.luckykuang.excel.service.StudentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author luckykuang
 * @since 2025/2/11 11:05
 */
@Service
@AllArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final ExportClient exportClient;
    private final ImportClient importClient;

    @Override
    public void exportStudentExcel(StudentQO studentQO) {
        ExportManagerDTO<StudentQO> exportManagerDTO = new ExportManagerDTO<>();
        exportManagerDTO.setExportBusiness(ExportBusiness.STUDENT_INFO);
        exportManagerDTO.setQuery(studentQO);
        exportClient.exportExcel(exportManagerDTO);
    }

    @Override
    public String importStudentExcel(MultipartFile file) {
        return importClient.importExcel(ImportBusiness.STUDENT_INFO.getCode(),file);
    }
}
