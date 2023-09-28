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

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author luckykuang
 * @date 2023/9/25 17:20
 */
class FileCopyUtilsTest {

    // 获取默认定位到的当前用户目录("user.dir"),也就是当前应用的根路径
    private static final String tempDir = System.getProperty("user.dir");
    private static final File fromImage = new File(tempDir + "\\images\\Gaara.jpg");
    private static final File fromFile = new File(tempDir + "\\test.txt");
    private static final File toFile = new File(tempDir + "\\test-temp.txt");

    @BeforeEach
    void setUp() throws IOException {
        if (!fromFile.exists()){
            fromFile.createNewFile();
        }
        if (!toFile.exists()){
            toFile.createNewFile();
        }
        String base64Content = FileConvertUtils.imageToBase64(fromImage.getAbsolutePath());
        FileWriterUtils.bytesToFileByDataOutputStream(base64Content.getBytes(),fromFile);
    }

    @AfterEach
    void tearDown() {
        if (fromFile.exists()){
            fromFile.delete();
        }
        if (toFile.exists()){
            toFile.delete();
        }
    }

    @Test
    void fileCopyNormal() throws IOException {
        long start = System.currentTimeMillis();
        FileCopyUtils.fileCopyNormal(fromFile,toFile);
        long end = System.currentTimeMillis();
        System.out.println("fileCopyNormal:" + (end - start));
        // 使用 Files API 读取文件内容并计算 MD5
        String file1 = DigestUtils.md5Hex(Files.readAllBytes(fromFile.toPath()));
        String file2 = DigestUtils.md5Hex(Files.readAllBytes(toFile.toPath()));

        assertEquals(file1,file2);
    }

    @Test
    void fileCopyWithFileChannelByTransferTo() throws IOException {
        long start = System.currentTimeMillis();
        FileCopyUtils.fileCopyWithFileChannelByTransferTo(fromFile,toFile);
        long end = System.currentTimeMillis();
        System.out.println("fileCopyWithFileChannelByTransferTo:" + (end - start));
        // 使用 Files API 读取文件内容并计算 MD5
        String file1 = DigestUtils.md5Hex(Files.readAllBytes(fromFile.toPath()));
        String file2 = DigestUtils.md5Hex(Files.readAllBytes(toFile.toPath()));
        assertEquals(file1,file2);
    }

    @Test
    void fileCopyWithFileChannelByTransferTo2() throws IOException {
        long start = System.currentTimeMillis();
        FileCopyUtils.fileCopyWithFileChannelByTransferTo2(fromFile,toFile);
        long end = System.currentTimeMillis();
        System.out.println("fileCopyWithFileChannelByTransferTo2:" + (end - start));
        // 使用 Files API 读取文件内容并计算 MD5
        String file1 = DigestUtils.md5Hex(Files.readAllBytes(fromFile.toPath()));
        String file2 = DigestUtils.md5Hex(Files.readAllBytes(toFile.toPath()));
        assertEquals(file1,file2);
    }

    @Test
    void fileCopyWithFileChannelByTransferFrom() throws IOException {
        long start = System.currentTimeMillis();
        FileCopyUtils.fileCopyWithFileChannelByTransferFrom(fromFile,toFile,0,fromFile.length());
        long end = System.currentTimeMillis();
        System.out.println("fileCopyWithFileChannelByTransferFrom:" + (end - start));
        // 使用 Files API 读取文件内容并计算 MD5
        String file1 = DigestUtils.md5Hex(Files.readAllBytes(fromFile.toPath()));
        String file2 = DigestUtils.md5Hex(Files.readAllBytes(toFile.toPath()));
        assertEquals(file1,file2);
    }

    @Test
    void fileCopyWithFileChannelByTransferFrom2() throws IOException {
        long start = System.currentTimeMillis();
        FileCopyUtils.fileCopyWithFileChannelByTransferFrom2(fromFile,toFile,0,fromFile.length());
        long end = System.currentTimeMillis();
        System.out.println("fileCopyWithFileChannelByTransferFrom2:" + (end - start));
        // 使用 Files API 读取文件内容并计算 MD5
        String file1 = DigestUtils.md5Hex(Files.readAllBytes(fromFile.toPath()));
        String file2 = DigestUtils.md5Hex(Files.readAllBytes(toFile.toPath()));
        assertEquals(file1,file2);
    }

    @Test
    void fileCopyWithFileChannelByByteBuffer() throws IOException {
        long start = System.currentTimeMillis();
        FileCopyUtils.fileCopyWithFileChannelByByteBuffer(fromFile,toFile);
        long end = System.currentTimeMillis();
        System.out.println("fileCopyWithFileChannelByByteBuffer:" + (end - start));
        // 使用 Files API 读取文件内容并计算 MD5
        String file1 = DigestUtils.md5Hex(Files.readAllBytes(fromFile.toPath()));
        String file2 = DigestUtils.md5Hex(Files.readAllBytes(toFile.toPath()));
        assertEquals(file1,file2);
    }

    @Test
    void fileCopyWithFileChannelByByteBuffer2() throws IOException {
        long start = System.currentTimeMillis();
        FileCopyUtils.fileCopyWithFileChannelByByteBuffer2(fromFile,toFile);
        long end = System.currentTimeMillis();
        System.out.println("fileCopyWithFileChannelByByteBuffer2:" + (end - start));
        // 使用 Files API 读取文件内容并计算 MD5
        String file1 = DigestUtils.md5Hex(Files.readAllBytes(fromFile.toPath()));
        String file2 = DigestUtils.md5Hex(Files.readAllBytes(toFile.toPath()));
        assertEquals(file1,file2);
    }

//    @Test
//    void fileCopyWithMappedByteBuffer() throws IOException {
//        long start = System.currentTimeMillis();
//        FileCopyUtils.fileCopyWithMappedByteBuffer(fromFile,toFile);
//        long end = System.currentTimeMillis();
//        System.out.println("fileCopyWithMappedByteBuffer:" + (end - start));
//        // 使用 Files API 读取文件内容并计算 MD5
//        String file1 = DigestUtils.md5Hex(Files.readAllBytes(fromFile.toPath()));
//        String file2 = DigestUtils.md5Hex(Files.readAllBytes(toFile.toPath()));
//        assertEquals(file1,file2);
//    }
}