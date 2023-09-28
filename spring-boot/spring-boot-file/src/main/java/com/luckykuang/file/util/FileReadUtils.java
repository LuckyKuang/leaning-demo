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
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 文件读取工具类
 * @author luckykuang
 * @date 2023/9/11 18:15
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileReadUtils {

    /**
     * 用BufferedReader读取
     * @param fromFile  源文件
     * @return          读取的字符串数据
     */
    public static List<String> readFileByBufferedReader(File fromFile){
        List<String> resultList = new ArrayList<>();
        try(FileReader fileReader = new FileReader(fromFile);
            BufferedReader reader = new BufferedReader(fileReader)) {
            String str;
            // 读取文件
            while ((str = reader.readLine()) != null) {
                resultList.add(str);
            }
            return resultList;
        } catch (IOException e){
            log.error("readFileByBufferedReader exception",e);
            return Collections.emptyList();
        }
    }

    /**
     * 用DataInputStream读取
     * @param fromFile  源文件
     * @return          读取的字符串数据
     */
    public static String readFileByDataInputStream(File fromFile){
        try(FileInputStream fis = new FileInputStream(fromFile);
            DataInputStream reader = new DataInputStream(fis)) {
            return reader.readUTF();
        } catch (IOException e){
            log.error("stringToFileByBufferedWriter exception",e);
            return null;
        }
    }

    /**
     * 用DataInputStream读取
     * @param fromFile  源文件
     * @return          读取的字节数组数据
     */
    public static byte[] readFileBytesByDataInputStream(File fromFile){
        try(FileInputStream fis = new FileInputStream(fromFile);
            DataInputStream reader = new DataInputStream(fis)) {
            return reader.readAllBytes();
        } catch (IOException e){
            log.error("stringToFileByBufferedWriter exception",e);
            return new byte[0];
        }
    }

    /**
     * 用BufferedInputStream读取
     * @param fromFile  源文件
     * @return          读取的字符串数据
     */
    public static String readFileByBufferedInputStream(File fromFile){
        try(FileInputStream fis = new FileInputStream(fromFile);
            BufferedInputStream bufferedOutputStream = new BufferedInputStream(fis);
            DataInputStream reader = new DataInputStream(bufferedOutputStream)) {
            return reader.readUTF();
        } catch (IOException e){
            log.error("stringToFileByBufferedWriter exception",e);
            return null;
        }
    }

    /**
     * 用RandomAccessFile读取
     * @param fromFile  源文件
     * @param position  读取位置
     * @return          读取的字符串数据
     */
    public static String readFileByRandomAccessFile(File fromFile, long position){
        try(RandomAccessFile reader = new RandomAccessFile(fromFile, "r")) {
            // 读取位置
            reader.seek(position);
            // 读取文件
            return reader.readUTF();
        } catch (IOException e){
            log.error("readFileByRandomAccessFile exception",e);
            return null;
        }
    }

    public static String readFileByFileChannel(File toFile){
        try(RandomAccessFile fileInputStream = new RandomAccessFile(toFile, "rw");
            FileChannel fileChannelInput = fileInputStream.getChannel()) {

            // 文件编码是utf8,需要用utf8解码
            CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();

            // 分配一个直接缓冲区
            ByteBuffer directByteBuffer = ByteBuffer.allocate(1024);
            CharBuffer directCharBuffer = CharBuffer.allocate(1024);

            int readBytes = fileChannelInput.read(directByteBuffer);
            StringBuilder sb = new StringBuilder();
            // 临时存放转码后的字符
            char[] tmp;
            // 存放decode操作后未处理完的字节。decode仅仅转码尽可能多的字节，此次转码不了的字节需要缓存，下次再转
            byte[] remainByte;
            // 未转码的字节数
            int leftNum;
            while (readBytes != -1){
                // 切换buffer从写模式到读模式
                directByteBuffer.flip();
                // 以utf8编码转换ByteBuffer到CharBuffer
                decoder.decode(directByteBuffer,directCharBuffer,true);
                // 切换buffer从写模式到读模式
                directCharBuffer.flip();
                remainByte = null;
                leftNum = directByteBuffer.limit() - directByteBuffer.position();
                if (leftNum > 0) { // 记录未转换完的字节
                    remainByte = new byte[leftNum];
                    directByteBuffer.get(remainByte, 0, leftNum);
                }

                // 输出已转换的字符
                tmp = new char[directCharBuffer.length()];
                while (directCharBuffer.hasRemaining()) {
                    directCharBuffer.get(tmp);
                    sb.append(new String(tmp));
                }
                // 清空缓冲区
                directByteBuffer.clear();
                directCharBuffer.clear();
                if (remainByte != null) {
                    directByteBuffer.put(remainByte); // 将未转换完的字节写入bBuf，与下次读取的byte一起转换
                }
                // 清空缓冲区后继续读文件
                readBytes = fileChannelInput.read(directByteBuffer);
            }
            return sb.toString();
        } catch (IOException e){
            log.error("stringToFileByFileChannel exception",e);
            return null;
        }
    }

    /**
     * 用Files类读取
     * @param fromFilePath  源文件路径
     * @return              读取的字符串数据(集合)
     */
    public static List<String> readFileByFiles(String fromFilePath){
        try {
            return Files.readAllLines(Paths.get(fromFilePath),StandardCharsets.UTF_8);
        } catch (IOException e){
            log.error("readFileByFiles exception",e);
            return Collections.emptyList();
        }
    }

    /**
     * 用Files类读取
     * @param fromFilePath  源文件路径
     * @return              读取的字符串数据(字符串)
     */
    public static String readFileStringByFiles(String fromFilePath){
        try {
            return Files.readString(Paths.get(fromFilePath));
        } catch (IOException e){
            log.error("readFileStringByFiles exception",e);
            return null;
        }
    }
}
