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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author luckykuang
 * @date 2023/9/25 15:51
 */
class FileConvertUtilsTest {

    // 获取默认定位到的当前用户目录("user.dir"),也就是当前应用的根路径
    private static final String tempDir = System.getProperty("user.dir");
    private static final File toFile = new File(tempDir + "\\test.txt");
    private static final File toImage = new File(tempDir + "\\images\\Gaara.jpg");
    private static final File tempImage = new File(tempDir + "\\images\\Gaara-temp.jpg");

    @BeforeEach
    void setUp() throws IOException {
        if (!toFile.exists()){
            toFile.createNewFile();
        }
    }

    @AfterEach
    void tearDown() {
        if (toFile.exists()){
            toFile.delete();
        }
        if (tempImage.exists()){
            tempImage.delete();
        }
    }

    @Test
    void multipartFileToFile() throws IOException {
        MultipartFile multipartFile = new MockMultipartFile(toFile.getName(), new FileInputStream(toFile));
        File file = FileConvertUtils.multipartFileToFile(multipartFile, toFile.getAbsolutePath());
        assertEquals(toFile,file);
    }

    @Test
    void imageToBase64() {
        String base64Content = FileConvertUtils.imageToBase64(toImage.getPath());
        boolean verified = FileConvertUtils.verifyImage(toImage.getAbsolutePath(), base64Content);
        assertTrue(verified);
    }

    @Test
    void base64ToImage() throws NoSuchAlgorithmException {
        String base64Content = FileConvertUtils.imageToBase64(toImage.getPath());
        FileConvertUtils.base64ToImage(base64Content, tempImage.getAbsolutePath());

        MessageDigest md5 = MessageDigest.getInstance("md5");
        String img1 = new String(md5.digest(FileReadUtils.readFileBytesByDataInputStream(toImage)));
        String img2 = new String(md5.digest(FileReadUtils.readFileBytesByDataInputStream(tempImage)));

        assertEquals(img1,img2);
    }
}