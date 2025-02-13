package com.luckykuang.excel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luckykuang.excel.mapper.StudentMapper;
import com.luckykuang.excel.model.entity.Student;
import com.luckykuang.excel.service.StudentService;
import org.springframework.stereotype.Service;

/**
 * @author luckykuang
 * @since 2025/2/11 16:59
 */
@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {
}
