package com.luckykuang.excel.processor.factory;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.luckykuang.excel.aspect.ExportExcel;
import com.luckykuang.excel.enums.ExportBusiness;
import com.luckykuang.excel.mapper.TeacherMapper;
import com.luckykuang.excel.model.dto.ExportManagerDTO;
import com.luckykuang.excel.model.entity.Teacher;
import com.luckykuang.excel.model.qo.TeacherQO;
import com.luckykuang.excel.model.vo.TeacherVO;
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
@ExportExcel(ExportBusiness.TEACHER_INFO)
public class TeacherExportProcessor implements ExportProcessor {

    private final TeacherMapper teacherMapper;

    @Override
    public List<?> process(ExportManagerDTO<?> exportManagerDTO) {
        TeacherQO teacherQO = JSON.parseObject(JSON.toJSONString(exportManagerDTO.getQuery()), TeacherQO.class);
        Page<Teacher> page = new Page<>(teacherQO.getPageNum(),teacherQO.getPageSize());
        Page<Teacher> teacherPage = teacherMapper.selectPage(page, new LambdaQueryWrapper<Teacher>()
                .like(StringUtils.isNotBlank(teacherQO.getName()), Teacher::getName, teacherQO.getName()));
        AtomicInteger index = new AtomicInteger(1);
        return teacherPage.getRecords().stream().map(item -> {
            TeacherVO teacherVO = new TeacherVO();
            BeanUtils.copyProperties(item, teacherVO);
            teacherVO.setReqNum(index.getAndIncrement());
            return teacherVO;
        }).toList();
    }
}
