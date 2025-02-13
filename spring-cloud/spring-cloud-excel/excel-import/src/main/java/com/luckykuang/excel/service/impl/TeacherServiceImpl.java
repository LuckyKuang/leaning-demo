package com.luckykuang.excel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luckykuang.excel.mapper.TeacherMapper;
import com.luckykuang.excel.model.entity.Teacher;
import com.luckykuang.excel.service.TeacherService;
import org.springframework.stereotype.Service;

/**
 * @author luckykuang
 * @since 2025/2/11 17:26
 */
@Service
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements TeacherService {
}
