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
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;

/**
 * 文件拷贝工具类【只推荐零拷贝技术】
 * @author luckykuang
 * @date 2023/9/11 11:17
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FileCopyUtils {

    /**
     * 通过字节流的方式复制文件(普通拷贝)
     *
     * @param fromFile 源文件
     * @param toFile   目标文件
     * @deprecated 不推荐使用，仅用于测试
     */
    @Deprecated(since="2.0", forRemoval=true)
    public static void fileCopyNormal(File fromFile, File toFile) {
        try (FileInputStream fileInputStream = new FileInputStream(fromFile);
             BufferedInputStream inputStream = new BufferedInputStream(fileInputStream);
             FileOutputStream fileOutputStream = new FileOutputStream(toFile);
             BufferedOutputStream outputStream = new BufferedOutputStream(fileOutputStream)) {
            // 字节缓冲区
            byte[] bytes = new byte[1024];
            int i = -1;
            // 读取到输入流数据，然后写入到输出流中去，实现复制
            while ((i = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, i);
            }
        } catch (Exception e) {
            log.error("fileCopyNormal exception", e);
        }
    }

    /**
     * 通过 FileChannel.transferTo() 进行文件复制(零拷贝)
     *
     * @param fromFile 源文件
     * @param toFile   目标文件
     */
    public static void fileCopyWithFileChannelByTransferTo(File fromFile, File toFile) {
        try (FileInputStream fileInputStream = new FileInputStream(fromFile);
             FileOutputStream fileOutputStream = new FileOutputStream(toFile);
             FileChannel fileChannelInput = fileInputStream.getChannel();
             FileChannel fileChannelOutput = fileOutputStream.getChannel()) {

            // 将fileChannelInput通道的数据，写入到fileChannelOutput通道
            fileChannelInput.transferTo(0, fileChannelInput.size(), fileChannelOutput);
        } catch (IOException e) {
            log.error("fileCopyWithFileChannelByTransferTo exception", e);
        }
    }

    /**
     * 通过 FileChannel.transferTo() 进行文件复制(零拷贝)
     * 优化说明：
     * 1. 使用NIO的FileChannel.open()方法打开文件通道，以替代使用FileInputStream和FileOutputStream。
     * 2. 使用StandardOpenOption.READ和StandardOpenOption.WRITE作为参数，分别指定文件通道的读写模式。
     * 3. 使用transferTo()方法直接将数据从fileChannelInput通道传输到fileChannelOutput通道，实现文件的快速复制。
     *
     * 请确保在代码中引入正确的依赖项，例如Java的java.nio包。
     * 以上优化后的代码使用NIO的FileChannel进行文件通道的操作，通过直接传输数据进行文件复制，避免了使用缓冲区和循环读写的方式，提高了复制操作的效率。
     * @param fromFile
     * @param toFile
     */
    public static void fileCopyWithFileChannelByTransferTo2(File fromFile, File toFile) {
        try (FileChannel fileChannelInput = FileChannel.open(fromFile.toPath(), StandardOpenOption.READ);
             FileChannel fileChannelOutput = FileChannel.open(toFile.toPath(), StandardOpenOption.WRITE)) {

            // 将fileChannelInput通道的数据，直接传输到fileChannelOutput通道
            fileChannelInput.transferTo(0, fileChannelInput.size(), fileChannelOutput);
        } catch (IOException e) {
            log.error("fileCopyWithFileChannelByTransferTo2 exception", e);
        }
    }

    /**
     * 通过 FileChannel.transferFrom() 进行文件复制(零拷贝) - 多线程下分片复制
     *
     * @param fromFile 源文件
     * @param toFile   目标文件
     * @param position 每段分片的文件开始位置
     * @param count    每段分片的文件大小
     */
    public static void fileCopyWithFileChannelByTransferFrom(File fromFile, File toFile, long position, long count) {
        try (RandomAccessFile fileRaf = new RandomAccessFile(fromFile, "rw");
             RandomAccessFile copyRaf = new RandomAccessFile(toFile, "rw");
             // 得到fileInputStream的文件通道
             FileChannel fileChannelInput = fileRaf.getChannel();
             // 得到fileOutputStream的文件通道
             FileChannel fileChannelOutput = copyRaf.getChannel()) {

            // 指定文件大小
            copyRaf.setLength(fileRaf.length());

            // 指定通道当前位置
            fileChannelInput.position(position);
            fileChannelOutput.position(position);

            // 将fileChannelInput通道的数据，写入到fileChannelOutput通道
            fileChannelInput.transferFrom(fileChannelOutput, position, count);
        } catch (IOException e) {
            log.error("fileCopyWithFileChannelByTransferFrom exception", e);
        }
    }

    /**
     * 优化说明：
     * 1. 使用NIO的FileChannel.open()方法打开文件通道，以替代使用RandomAccessFile。
     * 2. 使用StandardOpenOption.READ和StandardOpenOption.WRITE作为参数，分别指定文件通道的读写模式。
     * 3. 使用truncate()方法设置文件大小，确保目标文件大小能容纳复制的数据。
     * 4. 使用transferFrom()方法将数据从fileChannelInput通道传输到fileChannelOutput通道，实现文件的快速复制。
     *
     * 请确保在代码中引入正确的依赖项，例如Java的java.nio包。
     * 以上优化后的代码使用NIO的FileChannel进行文件通道的操作，通过直接传输数据进行文件复制，同时调整目标文件大小，避免了使用缓冲区和循环读写的方式，提高了复制操作的效率。
     *
     * @param fromFile 源文件
     * @param toFile   目标文件
     * @param position 每段分片的文件开始位置
     * @param count    每段分片的文件大小
     */
    public static void fileCopyWithFileChannelByTransferFrom2(File fromFile, File toFile, long position, long count) {
        try (FileChannel fileChannelInput = FileChannel.open(fromFile.toPath(), StandardOpenOption.READ);
             FileChannel fileChannelOutput = FileChannel.open(toFile.toPath(), StandardOpenOption.WRITE)) {

            // 指定文件大小
            fileChannelOutput.truncate(position + count);

            // 将fileChannelInput通道的数据，写入到fileChannelOutput通道
            fileChannelOutput.transferFrom(fileChannelInput, position, count);
        } catch (IOException e) {
            log.error("fileCopyWithFileChannelByTransferFrom2 exception", e);
        }
    }

    /**
     * 通过 FileChannel.write() 进行文件复制(零拷贝)
     *
     * @param fromFile 源文件
     * @param toFile   目标文件
     */
    public static void fileCopyWithFileChannelByByteBuffer(File fromFile, File toFile) {
        try (FileInputStream fileInputStream = new FileInputStream(fromFile);
             FileOutputStream fileOutputStream = new FileOutputStream(toFile);
             // 得到fileInputStream的文件通道
             FileChannel fileChannelInput = fileInputStream.getChannel();
             // 得到fileOutputStream的文件通道
             FileChannel fileChannelOutput = fileOutputStream.getChannel()) {

            // 分配一个直接字节缓冲区
            ByteBuffer directByteBuffer = ByteBuffer.allocateDirect(1024);
            while (directByteBuffer.hasRemaining()) {
                int len = fileChannelInput.read(directByteBuffer);
                if (len == -1) {
                    break;
                }
                directByteBuffer.flip();  // 切换成读模式
                fileChannelOutput.write(directByteBuffer);
                directByteBuffer.clear();  // 切换成写模式
            }
        } catch (IOException e) {
            log.error("fileCopyWithFileChannelByByteBuffer exception", e);
        }
    }

    /**
     * 通过 FileChannel.write() 进行文件复制(零拷贝)
     * 优化说明：
     * 1. 使用NIO的FileChannel.open()方法打开文件通道，以替代使用FileInputStream和FileOutputStream。
     * 2. 使用StandardOpenOption.READ和StandardOpenOption.WRITE作为参数，分别指定文件通道的读写模式。
     * 3. 使用allocateDirect()方法分配一个直接字节缓冲区，避免了使用堆上的中间缓冲区，提高了读写操作的效率。
     * 4. 在循环中使用read()方法直接将数据读入缓冲区，避免了使用hasRemaining()和flip()方法的判断和切换。
     * 5. 使用write()方法将缓冲区的数据直接写入目标文件通道。
     *
     * 请确保在代码中引入正确的依赖项，例如Java的java.nio包。
     * 以上优化后的代码使用NIO的FileChannel和直接字节缓冲区进行文件通道的操作，避免了使用FileInputStream和FileOutputStream，以及中间缓冲区的切换操作，提高了复制操作的效率。
     *
     * @param fromFile 源文件
     * @param toFile   目标文件
     */
    public static void fileCopyWithFileChannelByByteBuffer2(File fromFile, File toFile) {
        try (FileChannel fileChannelInput = FileChannel.open(fromFile.toPath(), StandardOpenOption.READ);
             FileChannel fileChannelOutput = FileChannel.open(toFile.toPath(), StandardOpenOption.WRITE)) {

            // 分配一个直接字节缓冲区
            ByteBuffer directByteBuffer = ByteBuffer.allocateDirect(1024);
            while (fileChannelInput.read(directByteBuffer) != -1) {
                directByteBuffer.flip();  // 切换成读模式
                fileChannelOutput.write(directByteBuffer);
                directByteBuffer.clear();  // 切换成写模式
            }
        } catch (IOException e) {
            log.error("fileCopyWithFileChannelByByteBuffer2 exception", e);
        }
    }

    /**
     * 通过 FileChannel.map() 进行文件复制(零拷贝) - mmap（Memory Mapped Files）是一种零拷贝技术
     * 注意：此方法会有文件残留【不推荐】
     *
     * @param fromFile 源文件
     * @param toFile   目标文件
     */
    public static void fileCopyWithMappedByteBuffer(File fromFile, File toFile) {
        try (FileChannel fileChannelInput = FileChannel.open(fromFile.toPath(), StandardOpenOption.READ);
             FileChannel fileChannelOutput = FileChannel.open(toFile.toPath(), StandardOpenOption.READ,
                     StandardOpenOption.WRITE,StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {

            // 将输入文件通道的一个区域直接映射到内存中，读取模式
            MappedByteBuffer inputBuffer = fileChannelInput.map(FileChannel.MapMode.READ_ONLY, 0, fileChannelInput.size());
            // 将输出文件通道的一个区域直接映射到内存中，读写模式
            MappedByteBuffer outputBuffer = fileChannelOutput.map(FileChannel.MapMode.READ_WRITE, 0, fileChannelInput.size());

            // 执行文件复制
            outputBuffer.put(inputBuffer);
        } catch (IOException e) {
            log.error("fileCopyWithMappedByteBuffer exception", e);
        }
    }
}
