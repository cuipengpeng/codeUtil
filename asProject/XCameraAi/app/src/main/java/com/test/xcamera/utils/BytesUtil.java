/******************************************************************************
 *                                                                            *
 * Copyright (c) 2011 by TUTK Co.LTD. All Rights Reserved.                    *
 *                                                                            *
 *                                                                            *
 * Class: BytesUtil.java                                                         *
 *                                                                            *
 * Author: joshua ju                                                          *
 *                                                                            *
 * Date: 2011-05-14                                                           *
 *                                                                            *
 ******************************************************************************/

package com.test.xcamera.utils;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class BytesUtil {

    public static final short byteArrayToShort(byte byt[], int nBeginPos, boolean bigOrder) {
        if (bigOrder) {
            return (short) ((0xff & byt[nBeginPos]) << 8 | 0xff & byt[nBeginPos + 1]);
        } else {
            return (short) ((0xff & byt[nBeginPos]) | ((0xff & byt[nBeginPos + 1]) << 8));
        }
    }

    public static final int byteArrayToInt(byte byt[], int nBeginPos, boolean bigOrder) {
        if (bigOrder) {
            return (0xff & byt[nBeginPos]) << 24 | (0xff & byt[nBeginPos + 1]) << 16 | (0xff & byt[nBeginPos + 2]) << 8 | 0xff & byt[nBeginPos + 3];
        } else {
            return (0xff & byt[nBeginPos]) | (0xff & byt[nBeginPos + 1]) << 8 | (0xff & byt[nBeginPos + 2]) << 16 | (0xff & byt[nBeginPos + 3]) << 24;

        }
    }

    public static final long byteArrayToLong(byte byt[], int nBeginPos, boolean bigOrder) {
        if (bigOrder) {
            return (long) ((0xff & byt[nBeginPos]) << 56 | (0xff & byt[nBeginPos + 1]) << 48 | (0xff & byt[nBeginPos + 2]) << 40 | (0xff & byt[nBeginPos + 3]) << 32 | (0xff & byt[nBeginPos + 4]) << 24 | (0xff & byt[nBeginPos + 5]) << 16 | (0xff & byt[nBeginPos + 6]) << 8 | 0xff & byt[nBeginPos + 7]);
        } else {
            return (long) (0xff & byt[nBeginPos] | (0xff & byt[nBeginPos + 1]) << 8 | (0xff & byt[nBeginPos + 2]) << 16 | (0xff & byt[nBeginPos + 3]) << 24 | (0xff & byt[nBeginPos + 4]) << 32 | (0xff & byt[nBeginPos + 5]) << 40 | (0xff & byt[nBeginPos + 6]) << 48 | (0xff & byt[nBeginPos + 7]) << 56);
        }
    }

    public static final byte[] shortToByteArray(short value, boolean bigOrder) {
        if (bigOrder) {
            return new byte[]{(byte) (value >>> 8), (byte) value};
        } else {
            return new byte[]{(byte) value, (byte) (value >>> 8)};
        }
    }

    public static final byte[] intToByteArray(int value, boolean bigOrder) {
        if (bigOrder) {
            return new byte[]{(byte) (value >>> 24), (byte) (value >>> 16), (byte) (value >>> 8), (byte) value};
        } else {
            return new byte[]{(byte) value, (byte) (value >>> 8), (byte) (value >>> 16), (byte) (value >>> 24)};
        }
    }

    public static long longFrom8Bytes(byte[] input, int offset, boolean littleEndian) {
        long value = 0;
        // 循环读取每个字节通过移位运算完成long的8个字节拼装
        for (int count = 0; count < 8; ++count) {
            int shift = (littleEndian ? count : (7 - count)) << 3;
            value |= ((long) 0xff << shift) & ((long) input[offset + count] << shift);
        }
        return value;
    }

    public static final String byteToString(byte byt[], int nBeginPos, boolean bigOrder) {
        short sh = BytesUtil.byteArrayToShort(byt, nBeginPos, bigOrder);
        byte[] bytes = BytesUtil.shortToByteArray(sh, false);
        try {
            return new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs = new byte[count];
        System.arraycopy(src, begin, bs, 0, count);
        return bs;
    }

    public static byte[] byteMergerAll(byte[]... values) {
        int length_byte = 0;
        for (int i = 0; i < values.length; i++) {
            length_byte += values[i].length;
        }
        byte[] all_byte = new byte[length_byte];
        int countLength = 0;
        for (int i = 0; i < values.length; i++) {
            byte[] b = values[i];
            System.arraycopy(b, 0, all_byte, countLength, b.length);
            countLength += b.length;
        }
        return all_byte;
    }

    public static byte[] concatAll(byte[] first, byte[]... rest) {
        int totalLength = first.length;
        for (byte[] array : rest) {
            totalLength += array.length;
        }
        byte[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (byte[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

    public static byte[] intToBytes(int a, int length) {
        byte[] bs = new byte[length];
        for (int i = bs.length - 1; i >= 0; i--) {
            bs[i] = (byte) (a % 255);
            a = a / 255;
        }
        return bs;
    }


    /**
     * byte[]-->hexString
     *
     * @param bytes
     * @return
     */
    public static String toHexString(byte[] bytes, int len) {
        int length = len == -1 ? bytes.length : len;
        StringBuilder sb = new StringBuilder(length * 2);
        // 使用String的format方法进行转换
        for (int i = 0; i < length; i++) {
            sb.append(String.format("%d:%02x ", i, new Integer(bytes[i] & 0xff)));
        }

        return sb.toString();
    }
}
