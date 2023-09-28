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

package com.luckykuang.file.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Base64;

/**
 * 文件转换工具类
 * @author luckykuang
 * @date 2023/9/11 15:04
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FileConvertUtils {

    /**
     * MultipartFile 转 File
     * @param fromFile  上传源文件
     * @param toPath    目标文件路径
     */
    public static File multipartFileToFile(MultipartFile fromFile, String toPath){
        File file = new File(toPath);
        try {
            if (!file.exists()){
                file.createNewFile();
            }
            fromFile.transferTo(file);
        } catch (IOException e) {
            log.error("multipartFileToFile exception",e);
        }
        return file;
    }

    /**
     * 图片 转 base64
     * Java生成的base64没有前端需要的前缀"data:image/png;base64,"，根据具体场景是否拼接
     * @param filePath 图片路径
     * @return base64编码
     */
    public static String imageToBase64(String filePath) {
        if (filePath == null) {
            return null;
        }
        try {
            Path path = Paths.get(filePath);
            File file = path.toFile();
            if (file.exists()){
                byte[] b = Files.readAllBytes(path);
                return Base64.getEncoder().encodeToString(b);
            }
        } catch (IOException e) {
            log.error("imageToBase64 exception",e);
        }
        return null;
    }

    /**
     * base64 转 图片
     * @param base64 base64编码(不含前缀)
     * @param filePath 图片路径
     * @return 成功-true 失败-false
     */
    public static boolean base64ToImage(String base64, String filePath) {
        if (base64 == null && filePath == null) {
            log.warn("生成文件失败，请给出相应的数据");
            return false;
        }
        try {
            Files.write(Paths.get(filePath), Base64.getDecoder().decode(base64), StandardOpenOption.CREATE);
        } catch (IOException e) {
            log.error("base64ToImage exception",e);
            return false;
        }
        return true;
    }

    /**
     * 校验原图片和原图片生成的base64是否一致
     * @param sourceFilePath    原图片
     * @param targetBase64      原图片生成的base64
     * @return                  true-相等 false-不相等
     */
    public static boolean verifyImage(String sourceFilePath, String targetBase64){
        // 将Base64编码的图像转换为字节数组
        byte[] targetImageBytes = Base64.getDecoder().decode(targetBase64);

        try {
            // 读取原图片为字节数组
            byte[] sourceImageBytes = Files.readAllBytes(Paths.get(sourceFilePath));

            // 比较两个字节数组
            boolean isSame = compareImages(sourceImageBytes, targetImageBytes);

            if (isSame) {
                log.info("The two images are identical.");
                return true;
            } else {
                log.info("The two images are different.");
                return false;
            }
        } catch (IOException e) {
            log.error("图片校验异常",e);
            return false;
        }
    }

    /**
     * 对比图片
     */
    private static boolean compareImages(byte[] sourceImageBytes, byte[] targetImageBytes) {
        if (sourceImageBytes.length != targetImageBytes.length) {
            return false;
        }
        for (int i = 0; i < sourceImageBytes.length; i++) {
            if (sourceImageBytes[i] != targetImageBytes[i]) {
                return false;
            }
        }
        return true;
    }
}
