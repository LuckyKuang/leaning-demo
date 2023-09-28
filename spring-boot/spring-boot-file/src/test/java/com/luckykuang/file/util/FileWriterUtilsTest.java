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

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author luckykuang
 * @date 2023/9/19 16:49
 */
class FileWriterUtilsTest {

    // 获取默认定位到的当前用户目录("user.dir"),也就是当前应用的根路径
    private static final String tempDir = System.getProperty("user.dir");
    private static final String fromData = "1234567890，。；：,.;:中文";
    private static final File toFile = new File(tempDir + "\\test.txt");

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
    }

    @Test
    void stringToFileByBufferedWriter() {
        FileWriterUtils.stringToFileByBufferedWriter(fromData, toFile);
        List<String> readContentList = Objects.requireNonNull(FileReadUtils.readFileByBufferedReader(toFile));
        String joinContent = String.join(",", readContentList);
        assertEquals(fromData,joinContent);

        FileWriterUtils.stringToFileByBufferedWriter(fromData + "\r\n" + fromData + fromData, toFile);
        List<String> readContentList2 = Objects.requireNonNull(FileReadUtils.readFileByBufferedReader(toFile));
        String joinContent2 = String.join(",", readContentList2);
        assertEquals(fromData + "," + fromData + fromData,joinContent2);

        FileWriterUtils.stringToFileByBufferedWriter(fromData + "," + fromData, toFile);
        List<String> readContentList3 = Objects.requireNonNull(FileReadUtils.readFileByBufferedReader(toFile));
        String joinContent3 = String.join(",", readContentList3);
        assertEquals(fromData + "," + fromData,joinContent3);
    }

    @Test
    void stringAppendFileByBufferedWriter() {
        FileWriterUtils.stringToFileByBufferedWriter(fromData, toFile);
        FileWriterUtils.stringAppendFileByBufferedWriter(toFile, fromData,",", fromData + fromData);
        List<String> readContentList = Objects.requireNonNull(FileReadUtils.readFileByBufferedReader(toFile));
        String joinContent = String.join(",", readContentList);
        assertEquals(fromData + "," + fromData + fromData,joinContent);

        FileWriterUtils.stringToFileByBufferedWriter(fromData, toFile);
        FileWriterUtils.stringAppendFileByBufferedWriter(toFile, fromData,"\r\n", fromData);
        List<String> readContentList2 = Objects.requireNonNull(FileReadUtils.readFileByBufferedReader(toFile));
        String joinContent2 = String.join(",", readContentList2);
        assertEquals(fromData + "," + fromData,joinContent2);
    }

    @Test
    void stringToFileByPrintWriter() {
        FileWriterUtils.stringToFileByPrintWriter(fromData, toFile);
        List<String> readContentList = FileReadUtils.readFileByFiles(toFile.getAbsolutePath());
        String joinContent = String.join(",", readContentList);
        assertEquals(fromData,joinContent);

        FileWriterUtils.stringToFileByPrintWriter(fromData + "\r\n" + fromData + fromData, toFile);
        List<String> readContentList2 = FileReadUtils.readFileByFiles(toFile.getAbsolutePath());
        String joinContent2 = String.join(",", readContentList2);
        assertEquals(fromData + "," + fromData + fromData,joinContent2);

        FileWriterUtils.stringToFileByPrintWriter(fromData + "," + fromData, toFile);
        List<String> readContentList3 = FileReadUtils.readFileByFiles(toFile.getAbsolutePath());
        String joinContent3 = String.join(",", readContentList3);
        assertEquals(fromData + "," + fromData,joinContent3);
    }

    /**
     * DataOutputStream写入什么类型的数据，DataInputStream就只能读取什么类型的数据，两者一定要匹配
     */
    @Test
    void stringToFileByFileOutputStream() {
        FileWriterUtils.stringToFileByDataOutputStream(fromData,toFile);
        String readContent = FileReadUtils.readFileByDataInputStream(toFile);
        assertEquals(fromData,readContent);

        FileWriterUtils.stringToFileByDataOutputStream(fromData + "\r\n" + fromData + fromData,toFile);
        String readContent2 = FileReadUtils.readFileByDataInputStream(toFile);
        assertEquals(fromData + "\r\n" + fromData + fromData,readContent2);

        FileWriterUtils.stringToFileByDataOutputStream(fromData + "," + fromData,toFile);
        String readContent3 = FileReadUtils.readFileByDataInputStream(toFile);
        assertEquals(fromData + "," + fromData,readContent3);
    }

    /**
     * DataOutputStream写入什么类型的数据，DataInputStream就只能读取什么类型的数据，两者一定要匹配
     */
    @Test
    void stringToFileByDataOutputStream() {
        FileWriterUtils.stringToFileByBufferedOutputStream(fromData,toFile);
        String readContent = FileReadUtils.readFileByBufferedInputStream(toFile);
        assertEquals(fromData,readContent);

        FileWriterUtils.stringToFileByBufferedOutputStream(fromData + "\r\n" + fromData + fromData,toFile);
        String readContent2 = FileReadUtils.readFileByBufferedInputStream(toFile);
        assertEquals(fromData + "\r\n" + fromData + fromData,readContent2);

        FileWriterUtils.stringToFileByBufferedOutputStream(fromData + "," + fromData,toFile);
        String readContent3 = FileReadUtils.readFileByBufferedInputStream(toFile);
        assertEquals(fromData + "," + fromData,readContent3);
    }

    /**
     * RandomAccessFile写入什么类型的数据，RandomAccessFile就只能读取什么类型的数据，两者一定要匹配
     */
    @Test
    void stringToFileByRandomAccessFile() {
        FileWriterUtils.stringToFileByRandomAccessFile(fromData,toFile,0);
        String readContent = FileReadUtils.readFileByRandomAccessFile(toFile, 0);
        assertEquals(fromData,readContent);

        FileWriterUtils.stringToFileByRandomAccessFile(fromData + "\r\n" + fromData + fromData,toFile,0);
        String readContent2 = FileReadUtils.readFileByRandomAccessFile(toFile, 0);
        assertEquals(fromData + "\r\n" + fromData + fromData,readContent2);

        FileWriterUtils.stringToFileByRandomAccessFile(fromData + "," + fromData,toFile,0);
        String readContent3 = FileReadUtils.readFileByRandomAccessFile(toFile, 0);
        assertEquals(fromData + "," + fromData,readContent3);
    }

    @Test
    void stringToFileByFileChannel() {
        FileWriterUtils.stringToFileByFileChannel(fromData,toFile);
        String readContent = FileReadUtils.readFileByFileChannel(toFile);
        assertEquals(fromData,readContent);

        FileWriterUtils.stringToFileByFileChannel(fromData + "\r\n" + fromData + fromData,toFile);
        String readContent2 = FileReadUtils.readFileByFileChannel(toFile);
        assertEquals(fromData + "\r\n" + fromData + fromData,readContent2);

        FileWriterUtils.stringToFileByFileChannel(fromData + "," + fromData,toFile);
        String readContent3 = FileReadUtils.readFileByFileChannel(toFile);
        assertEquals(fromData + "," + fromData,readContent3);
    }

    /**
     * Files是覆盖写入
     * 注意：如果本次的写入数据长度低于前一次的数据长度，此时就会出现问题
     * 比如：第一次写入"123456",第二次写入"abcd",此时读取就会出现问题，因为文件内容已经变成了"abcd<x87>56"
     */
    @Test
    void stringToFileByFiles() {
        FileWriterUtils.stringToFileByFiles(fromData,toFile.getPath());
        String readContent = FileReadUtils.readFileStringByFiles(toFile.getPath());
        assertEquals(fromData,readContent);

        FileWriterUtils.stringToFileByFiles(fromData + "\r\n" + fromData + fromData,toFile.getPath());
        String readContent2 = FileReadUtils.readFileStringByFiles(toFile.getPath());
        assertEquals(fromData + "\r\n" + fromData + fromData,readContent2);

        // 因为上面的写入数据长度大于下面的写入数据长度，会报错
        // 注释掉此行，将会复现问题
        toFile.delete();

        FileWriterUtils.stringToFileByFiles(fromData + "," + fromData,toFile.getPath());
        String readContent3 = FileReadUtils.readFileStringByFiles(toFile.getPath());
        assertEquals(fromData + "," + fromData,readContent3);
    }

    @Test
    void stringToFileByFileLock() {
        FileWriterUtils.stringToFileByFileLock(fromData,toFile);
        String readContent = FileReadUtils.readFileByRandomAccessFile(toFile,0);
        assertEquals(fromData,readContent);

        FileWriterUtils.stringToFileByFileLock(fromData + "\r\n" + fromData + fromData,toFile);
        String readContent2 = FileReadUtils.readFileByRandomAccessFile(toFile,0);
        assertEquals(fromData + "\r\n" + fromData + fromData,readContent2);

        FileWriterUtils.stringToFileByFileLock(fromData + "," + fromData,toFile);
        String readContent3 = FileReadUtils.readFileByRandomAccessFile(toFile,0);
        assertEquals(fromData + "," + fromData,readContent3);
    }
}