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
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author luckykuang
 * @date 2023/10/20 13:58
 */
@RestController
@RequestMapping("file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping(value = "/binaryUpload")
    public void binaryUpload(HttpServletRequest request) throws IOException {
        fileService.binaryUpload(request);
    }

    @PostMapping(value = "/binaryShardingUpload")
    public void binaryShardingUpload(HttpServletRequest request) throws IOException {
        fileService.binaryShardingUpload(request);
    }

    @PostMapping(value = "/formDataUpload")
    public void formDataUpload(@RequestPart MultipartFile file) throws IOException {
        fileService.formDataUpload(file);
    }

    @PostMapping(value = "/formDataUploads")
    public void formDataUploads(@RequestPart MultipartFile[] files) throws IOException {
        fileService.formDataUploads(files);
    }
}
