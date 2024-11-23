/*
 * Copyright 2015-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.luckykuang.thread.service.impl;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luckykuang.thread.entity.User;
import com.luckykuang.thread.mapper.UserMapper;
import com.luckykuang.thread.service.UserService;
import com.luckykuang.thread.utils.ResponseUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * @author luckykuang
 * @date 2023/7/5 10:56
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private UserService userService;

    @Override
    public void exportUserExcel(HttpServletResponse response) {
        try {
            ResponseUtils.setExcelResponseProp(response, "用户列表");
            List<User> userList = list();
            EasyExcelFactory.write(response.getOutputStream())
                    .head(User.class)
                    .excelType(ExcelTypeEnum.XLSX)
                    .sheet("用户列表")
                    .doWrite(userList);
        } catch (IOException e) {
            log.error("用户列表导出异常: ",e);
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ResponseEntity<List<User>> importUserExcel(MultipartFile file) {
        try {
            List<User> userList = EasyExcelFactory.read(file.getInputStream())
                    .head(User.class)
                    .sheet()
                    .doReadSync();
            userService.saveBatch(userList);
            return ResponseEntity.ok(userList);
        } catch (IOException e) {
            log.error("用户列表导入异常: ",e);
            throw new IllegalArgumentException(e);
        }
    }
}
