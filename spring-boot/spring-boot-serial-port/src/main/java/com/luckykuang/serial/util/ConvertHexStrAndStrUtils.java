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

package com.luckykuang.serial.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.nio.charset.StandardCharsets;

/**
 * 字符串转十六进制工具类
 * @author luckykuang
 * @date 2023/9/12 17:14
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConvertHexStrAndStrUtils {
    private static final char[] HEXES = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * 字节转十六进制
     * @param bytes 字节数组
     * @return 十六进制
     */
    public static String bytesToHexStr(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        StringBuilder hex = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            hex.append(HEXES[(b >> 4) & 0x0F]);
            hex.append(HEXES[b & 0x0F]);
        }
        return hex.toString().toUpperCase();
    }

    /**
     * 十六进制转字节数组
     * @param hex 十六进制
     * @return 字节数组
     */
    public static byte[] hexStrToBytes(String hex) {
        if (hex == null || hex.length() == 0) {
            return new byte[0];
        }
        char[] hexChars = hex.toCharArray();
        byte[] bytes = new byte[hexChars.length / 2];   // 如果 hex 中的字符不是偶数个, 则忽略最后一个
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(String.valueOf(hexChars[i * 2]) + hexChars[i * 2 + 1], 16);
        }
        return bytes;
    }

    /**
     * 字符串转十六进制
     * @param str 字符串
     * @return 十六进制
     */
    public static String strToHexStr(String str) {
        StringBuilder sb = new StringBuilder();
        byte[] bs = str.getBytes();
        int bit;
        for (byte b : bs) {
            bit = (b & 0x0f0) >> 4;
            sb.append(HEXES[bit]);
            bit = b & 0x0f;
            sb.append(HEXES[bit]);
        }
        return sb.toString().trim();
    }

    /**
     * 十六进制转字符串
     * @param hexStr 十六进制
     * @return 字符串
     */
    public static String hexStrToStr(String hexStr) {
        // 能被16整除,肯定可以被2整除
        byte[] array = new byte[hexStr.length() / 2];
        try {
            for (int i = 0; i < array.length; i++) {
                array[i] = (byte) (0xff & Integer.parseInt(hexStr.substring(i * 2, i * 2 + 2), 16));
            }
            hexStr = new String(array, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return hexStr;
    }
}
