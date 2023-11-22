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

package com.luckykuang.file.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author luckykuang
 * @date 2023/10/20 14:29
 */
public interface FileService {
    void binaryUpload(HttpServletRequest request) throws IOException;

    void binaryShardingUpload(HttpServletRequest request) throws IOException;

    void formDataUpload(MultipartFile file) throws IOException;

    void formDataUploads(MultipartFile[] files) throws IOException;

    ResponseEntity<String> upload(MultipartFile file);

    void download(String filePath, HttpServletResponse response);
}
