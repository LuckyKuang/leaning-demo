package com.luckykuang.excel.service.impl;

import com.luckykuang.excel.enums.ExportBusiness;
import com.luckykuang.excel.enums.ImportBusiness;
import com.luckykuang.excel.feign.ExportClient;
import com.luckykuang.excel.feign.ImportClient;
import com.luckykuang.excel.model.dto.ExportManagerDTO;
import com.luckykuang.excel.model.qo.TeacherQO;
import com.luckykuang.excel.service.TeacherService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author luckykuang
 * @since 2025/2/12 10:02
 */
@Service
@AllArgsConstructor
public class TeacherServiceImpl implements TeacherService {

    private final ExportClient exportClient;
    private final ImportClient importClient;

    @Override
    public void exportTeacherExcel(TeacherQO teacherQO) {
        ExportManagerDTO<TeacherQO> exportManagerDTO = new ExportManagerDTO<>();
        exportManagerDTO.setExportBusiness(ExportBusiness.TEACHER_INFO);
        exportManagerDTO.setQuery(teacherQO);
        exportClient.exportExcel(exportManagerDTO);
    }

    @Override
    public String importTeacherExcel(MultipartFile file) {
        return importClient.importExcel(ImportBusiness.TEACHER_INFO.getCode(),file);
    }
}
