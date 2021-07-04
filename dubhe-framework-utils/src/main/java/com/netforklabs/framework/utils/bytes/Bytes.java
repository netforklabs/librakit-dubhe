/*
 * MIT License
 *
 * Copyright (c) 2021 Netforklabs
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

/* Create date: 2021/6/25 */

package com.netforklabs.framework.utils.bytes;

/**
 * Byte数组操作
 *
 * @author luotsforever
 * @email orvlas@foxmail.com
 */
@SuppressWarnings("JavaDoc")
public class Bytes {

    /**
     * int占用字节大小
     */
    public static final int INT_BYTE_SIZE           = 4;

    /**
     * long占用字节大小
     */
    public static final int LONG_BYTE_SIZE          = 8;

    public static byte[] copyOf(byte[] bytes, int offset)
    {
        return copyOf(bytes, offset, bytes.length);
    }

    public static byte[] copyOf(byte[] bytes, int offset, int end)
    {
        int size        = end - offset;
        byte[] target   = new byte[size];
        System.arraycopy(bytes, offset, target, 0, size);

        return target;
    }

    /**
     * int转byte[4]数组
     */
    public static byte[] toByte4(int i0)
    {
        byte[] bytes = new byte[4];

        bytes[3] = (byte) (i0       & 0xFF);
        bytes[2] = (byte) (i0 >> 8  & 0xFF);
        bytes[1] = (byte) (i0 >> 16 & 0xFF);
        bytes[0] = (byte) (i0 >> 24 & 0xFF);

        return bytes;
    }

    public static int toInt(byte[] array) {
        return toInt(array, 0);
    }

    /**
     * byte转int
     *
     * @param array  数组
     * @param offset 开始位置，从偏移位置开始往后推3位，总共4字节构成一个int
     * @return int
     */
    public static int toInt(byte[] array, int offset)
    {
        int i0 = array[offset]          & 0xFF;
        int i1 = array[offset + 1]      & 0xFF;
        int i2 = array[offset + 2]      & 0xFF;
        int i3 = array[offset + 3]      & 0xFF;

        return (i0 << 24) | (i1 << 16) | (i2 << 8) | i3;
    }

}
