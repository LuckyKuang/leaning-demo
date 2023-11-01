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

package com.luckykuang.file.service.impl;

import com.luckykuang.file.service.FileService;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author luckykuang
 * @date 2023/10/20 14:29
 */
@Slf4j
@Service
public class FileServiceImpl implements FileService {
    private static final String tempDir = System.getProperty("user.dir");

    @Override
    public void binaryUpload(HttpServletRequest request) throws IOException {
        // 如：test
        String fileName = request.getHeader("file-name");
        // 如：.txt
        String fileSuffix = request.getHeader("file-suffix");
        File targetFile = new File(tempDir + File.separator + fileName + fileSuffix);
        if (!targetFile.exists()){
            targetFile.createNewFile();
        }
        ServletInputStream inputStream = request.getInputStream();
        // org.apache.commons.io.FileUtils
        FileUtils.copyInputStreamToFile(inputStream, targetFile);
        log.info("文件保存完成！");
    }

    @Override
    public void binaryShardingUpload(HttpServletRequest request) throws IOException {
        ServletInputStream inputStream = request.getInputStream();
        byte[] fileContent = inputStream.readAllBytes();
        // 定义每次保存的文件块大小（1MB）
        int chunkSize = 1024 * 1024;

        // 计算文件块数量
        int totalChunks = (int) Math.ceil((double) fileContent.length / chunkSize);

        // 创建输出文件目录
        File outputDir = new File(tempDir + File.separator + "output");
        if (!outputDir.exists()) {
            outputDir.mkdir();
        }

        // 分块保存文件内容
        for (int i = 0; i < totalChunks; i++) {
            // 计算当前块的起始位置和结束位置
            int start = i * chunkSize;
            int end = Math.min(start + chunkSize, fileContent.length);

            // 获取当前块的字节数组
            byte[] chunk = new byte[end - start];
            System.arraycopy(fileContent, start, chunk, 0, chunk.length);

            // 保存当前块的内容到文件
            saveChunkToFile(chunk, i);
        }
        log.info("文件保存完成！");
    }

    private static void saveChunkToFile(byte[] chunk, int chunkIndex) {
        // 生成文件名
        String fileName = tempDir + File.separator + "output/chunk_" + chunkIndex + ".log";

        // 保存文件内容到文件
        try (OutputStream outputStream = new FileOutputStream(fileName)) {
            outputStream.write(chunk);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void formDataUpload(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        if (StringUtils.isBlank(originalFilename)){
            return;
        }
        String fileName = originalFilename.substring(0,originalFilename.lastIndexOf("."));
        String fileSuffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        File targetFile = new File(tempDir + File.separator + fileName + fileSuffix);
        if (!targetFile.exists()){
            targetFile.createNewFile();
        }
        file.transferTo(targetFile);
        log.info("文件保存完成！");
    }

    @Override
    public void formDataUploads(MultipartFile[] files) throws IOException {
        for (MultipartFile file : files) {
            String originalFilename = file.getOriginalFilename();
            if (StringUtils.isBlank(originalFilename)){
                continue;
            }
            String fileName = originalFilename.substring(0,originalFilename.lastIndexOf("."));
            String fileSuffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            File targetFile = new File(tempDir + File.separator + fileName + fileSuffix);
            if (!targetFile.exists()){
                targetFile.createNewFile();
            }
            file.transferTo(targetFile);
        }
        log.info("文件保存完成！");
    }
}
