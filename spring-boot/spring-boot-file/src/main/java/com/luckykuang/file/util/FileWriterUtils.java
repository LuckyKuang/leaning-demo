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

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * 文件写入工具类
 * @author luckykuang
 * @date 2023/9/11 17:19
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FileWriterUtils {

    /**
     * 使用BufferedWriter写入
     * @param fromData  字符串数据
     * @param toFile    目标文件
     */
    public static void stringToFileByBufferedWriter(String fromData, File toFile){
        try(FileWriter fileWriter = new FileWriter(toFile);
            BufferedWriter writer = new BufferedWriter(fileWriter)) {
            // 字符串覆盖写入文件
            writer.write(fromData);
        } catch (IOException e){
            log.error("stringToFileByBufferedWriter exception",e);
        }
    }

    /**
     * 使用BufferedWriter写入 - 可以拼接
     * @param toFile        目标文件
     * @param fromData      需要拼接的字符串数据
     */
    public static void stringAppendFileByBufferedWriter(File toFile, String... fromData){
        try(FileWriter fileWriter = new FileWriter(toFile);
            BufferedWriter writer = new BufferedWriter(fileWriter)) {
            // 字符串附加写入文件
            for (String data : fromData) {
                writer.append(data);
            }
        } catch (IOException e){
            log.error("stringAppendFileByBufferedWriter exception",e);
        }
    }

    /**
     * 使用PrintWriter写入
     * @param fromData  字符串数据
     * @param toFile    目标文件
     */
    public static void stringToFileByPrintWriter(String fromData,File toFile){
        try(FileWriter fileWriter = new FileWriter(toFile);
            PrintWriter printWriter = new PrintWriter(fileWriter)) {

            // 字符串覆盖写入文件
            printWriter.print(fromData);
        } catch (IOException e){
            log.error("stringToFileByPrintWriter exception",e);
        }
    }

    /**
     * 使用DataOutputStream写入
     * @param fromData  字符串数据
     * @param toFile    目标文件
     */
    public static void stringToFileByDataOutputStream(String fromData,File toFile){
        try(FileOutputStream outputStream = new FileOutputStream(toFile);
            DataOutputStream outStream = new DataOutputStream(outputStream)) {
            // 字符串覆盖写入文件
            outStream.writeUTF(fromData);
        } catch (IOException e){
            log.error("stringToFileByFileOutputStream exception",e);
        }
    }

    /**
     * 使用DataOutputStream写入
     * @param fromData  字节数组数据
     * @param toFile    目标文件
     */
    public static void bytesToFileByDataOutputStream(byte[] fromData,File toFile){
        try(FileOutputStream outputStream = new FileOutputStream(toFile);
            DataOutputStream outStream = new DataOutputStream(outputStream)) {
            // 字符串覆盖写入文件
            outStream.write(fromData);
        } catch (IOException e){
            log.error("bytesToFileByDataOutputStream exception",e);
        }
    }

    /**
     * 使用BufferedOutputStream写入
     * @param fromData  字符串数据
     * @param toFile    目标文件
     */
    public static void stringToFileByBufferedOutputStream(String fromData,File toFile){
        try(FileOutputStream fos = new FileOutputStream(toFile);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fos);
            DataOutputStream outStream = new DataOutputStream(bufferedOutputStream)) {

            // 字符串覆盖写入文件
            outStream.writeUTF(fromData);
        } catch (IOException e){
            log.error("stringToFileByDataOutputStream exception",e);
        }
    }

    /**
     * 使用RandomAccessFile写入
     * @param fromData  字符串数据
     * @param toFile    目标文件
     * @param position  写入位置
     */
    public static void stringToFileByRandomAccessFile(String fromData,File toFile,long position){
        try(RandomAccessFile writer = new RandomAccessFile(toFile, "rw");) {

            // 写入位置
            writer.seek(position);
            // 字符串覆盖写入文件
            writer.writeUTF(fromData);
        } catch (IOException e){
            log.error("stringToFileByRandomAccessFile exception",e);
        }
    }

    /**
     * 使用FileChannel写入 【推荐】
     * @param fromData  字符串数据
     * @param toFile    目标文件
     */
    public static void stringToFileByFileChannel(String fromData,File toFile){
        try(RandomAccessFile fileOutputStream = new RandomAccessFile(toFile, "rw");
            FileChannel fileChannelOutput = fileOutputStream.getChannel()) {
            // 写入字符串字节
            byte[] strBytes = fromData.getBytes();
            // 分配一个直接缓冲区
            ByteBuffer directByteBuffer = ByteBuffer.allocate(1024);
            // 字符串字节写入直接缓冲区
            directByteBuffer.put(strBytes);
            // 切换成读模式
            directByteBuffer.flip();
            // 字符串覆盖写入文件
            while (directByteBuffer.hasRemaining()) {
                fileChannelOutput.write(directByteBuffer);
            }
            // 清空buffer，切换成写模式
            directByteBuffer.clear();
        } catch (IOException e){
            log.error("stringToFileByFileChannel exception",e);
        }
    }

    /**
     * 用Files类写入
     * @param fromData      字符串数据
     * @param toFilePath    目标文件路径
     */
    public static void stringToFileByFiles(String fromData,String toFilePath){
        try {
            // 字符串覆盖写入文件
            Files.writeString(Paths.get(toFilePath), fromData, StandardOpenOption.CREATE);
        } catch (IOException e){
            log.error("stringToFileByFiles exception",e);
        }
    }

    /**
     * 用FileLock写入，写入前锁定文件
     * @param fromData  字符串数据
     * @param toFile    目标文件
     */
    public static void stringToFileByFileLock(String fromData,File toFile){
        try(RandomAccessFile fileOutputStream = new RandomAccessFile(toFile, "rw");
            FileChannel fileChannelOutput = fileOutputStream.getChannel();
            // 写入文件前，先锁住该文件，防止其他方法写入
            FileLock ignored = fileChannelOutput.tryLock();) {

            // 字符串覆盖写入文件
            fileOutputStream.writeUTF(fromData);
        } catch (IOException e){
            log.error("stringToFileByFiles exception",e);
        }
    }
}
