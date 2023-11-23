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

import com.luckykuang.file.config.MinioProperties;
import com.luckykuang.file.service.FileService;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * @author luckykuang
 * @date 2023/10/20 14:29
 */
@Slf4j
@Service
public class FileServiceImpl implements FileService {

    /**
     * 项目路径
     */
    private static final String TEMP_DIR = System.getProperty("user.dir");

    /**
     * 日期格式化
     */
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("/yyyy/MM/dd/");

    @Resource
    private MinioProperties properties;
    @Resource
    private MinioClient client;

    @Override
    public void binaryUpload(HttpServletRequest request) throws IOException {
        // 如：test
        String fileName = request.getHeader("file-name");
        // 如：.txt
        String fileSuffix = request.getHeader("file-suffix");
        File targetFile = new File(TEMP_DIR + File.separator + fileName + fileSuffix);
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
        File outputDir = new File(TEMP_DIR + File.separator + "output");
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
        String fileName = TEMP_DIR + File.separator + "output/chunk_" + chunkIndex + ".log";

        // 保存文件内容到文件
        try (OutputStream outputStream = new FileOutputStream(fileName)) {
            outputStream.write(chunk);
        } catch (IOException e) {
            log.error("保存文件失败",e);
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
        File targetFile = new File(TEMP_DIR + File.separator + fileName + fileSuffix);
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
            File targetFile = new File(TEMP_DIR + File.separator + fileName + fileSuffix);
            if (!targetFile.exists()){
                targetFile.createNewFile();
            }
            file.transferTo(targetFile);
        }
        log.info("文件保存完成！");
    }

    @Override
    public ResponseEntity<String> upload(MultipartFile file) {
        String filePath = null;
        try (InputStream inputStream = file.getInputStream()) {
            // 文件大小
            long size = file.getSize();
            if (size == 0) {
                return ResponseEntity.badRequest().body("禁止上传空文件");
            }

            // 文件名称
            String fileName = file.getOriginalFilename();
            if (StringUtils.isBlank(fileName)) {
                return ResponseEntity.badRequest().body("禁止上传空白名称的文件");
            }

            int index = fileName.lastIndexOf(".");
            if (index ==-1) {
                return ResponseEntity.badRequest().body("禁止上传无后缀的文件");
            }

            // 文件后缀
            String ext = fileName.substring(index);

            // 文件类型
            String contentType = file.getContentType();
            if (contentType == null) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }

            // 根据日期打散目录，使用 UUID 重命名文件
            filePath = FORMATTER.format(LocalDate.now()) +
                    UUID.randomUUID().toString().replace("-", "") +
                    ext;

            log.info("文件名称：{}", fileName);
            log.info("文件大小：{}", size);
            log.info("文件类型：{}", contentType);
            log.info("文件路径：{}", filePath);

            // 上传文件到客户端
            client.putObject(PutObjectArgs.builder()
                    .bucket(properties.getBucket())  // 指定 Bucket
                    .contentType(contentType) // 指定 Content Type
                    .object(filePath)   // 指定文件的路径
                    .stream(inputStream, size, -1) // 文件的 InputStream 流
                    .build());
        } catch (Exception e){
            log.error("上传文件异常",e);
        }

        // 返回访问路径
        return ResponseEntity.ok(properties.getBaseUrl() + properties.getBucket() + filePath);
    }

    @Override
    public void download(String filePath, HttpServletResponse response) {
        String objectName = filePath.replace(properties.getEndpoint() + properties.getBucket(),"");
        String[] fileSplits = filePath.split("/");
        if (fileSplits.length == 0){
            throw new RuntimeException("文件路径有误");
        }
        String filename = fileSplits[fileSplits.length - 1];
        int index = filename.lastIndexOf(".");
        if (index == -1) {
            throw new RuntimeException("文件路径有误");
        }
        String fileNameUrl = URLEncoder.encode(filename, StandardCharsets.UTF_8);
        response.setHeader("Content-Disposition", "attachment;filename=" + fileNameUrl);
        response.addHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
        response.addHeader("X-Original-File-Name", filename);
        response.setContentType("application/octet-stream");

        GetObjectArgs args = GetObjectArgs.builder()
                .bucket(properties.getBucket())
                .object(objectName)
                .build();

        try(InputStream fileInputStream = client.getObject(args);
            ServletOutputStream fileOutputStream = response.getOutputStream()) {
            IOUtils.copy(fileInputStream, fileOutputStream);
            fileOutputStream.flush();
        } catch (Exception e) {
            log.error("下载文件异常",e);
            throw new RuntimeException("下载文件异常:" + e.getMessage());
        }
    }
}
