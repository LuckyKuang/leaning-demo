package com.luckykuang.excel.processor.factory;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.luckykuang.excel.aspect.ExportExcel;
import com.luckykuang.excel.enums.ExportBusiness;
import com.luckykuang.excel.mapper.StudentMapper;
import com.luckykuang.excel.model.dto.ExportManagerDTO;
import com.luckykuang.excel.model.entity.Student;
import com.luckykuang.excel.model.qo.StudentQO;
import com.luckykuang.excel.model.vo.StudentVO;
import com.luckykuang.excel.processor.ExportProcessor;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author luckykuang
 * @since 2025/2/11 15:43
 */
@Component
@AllArgsConstructor
@ExportExcel(ExportBusiness.STUDENT_INFO)
public class StudentExportProcessor implements ExportProcessor {

    private final StudentMapper studentMapper;

    @Override
    public List<?> process(ExportManagerDTO<?> exportManagerDTO) {
        StudentQO studentQO = JSON.parseObject(JSON.toJSONString(exportManagerDTO.getQuery()), StudentQO.class);
        Page<Student> page = new Page<>(studentQO.getPageNum(),studentQO.getPageSize());
        Page<Student> studentPage = studentMapper.selectPage(page, new LambdaQueryWrapper<Student>()
                .like(StringUtils.isNotBlank(studentQO.getName()), Student::getName, studentQO.getName()));
        AtomicInteger index = new AtomicInteger(1);
        return studentPage.getRecords().stream().map(item -> {
            StudentVO studentVO = new StudentVO();
            BeanUtils.copyProperties(item, studentVO);
            studentVO.setReqNum(index.getAndIncrement());
            return studentVO;
        }).toList();
    }
}
