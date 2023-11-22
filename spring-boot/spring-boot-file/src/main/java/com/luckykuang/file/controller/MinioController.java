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

package com.luckykuang.file.controller;

import com.luckykuang.file.service.FileService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author luckykuang
 * @date 2023/11/14 11:39
 */
@Slf4j
@RestController
@RequestMapping("minio")
@RequiredArgsConstructor
public class MinioController {

    private final FileService fileService;

    /**
     * 上传文件到 Minio 服务器
     * @param file 需要上传的文件
     * @return 上传文件的路径
     */
    @PostMapping(value = "upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) {
        return fileService.upload(file);
    }

    /**
     * 从 Minio 服务器下载文件
     * @param filePath 上传文件的路径
     * @param response http响应
     */
    @GetMapping(value = "download")
    public void download(String filePath, HttpServletResponse response) {
        fileService.download(filePath,response);
    }
}
