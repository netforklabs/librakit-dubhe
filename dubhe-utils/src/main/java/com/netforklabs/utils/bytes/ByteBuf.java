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

/* Create date: 2021/6/25. */

package com.netforklabs.utils.bytes;

import java.io.Serializable;
import java.nio.ByteBuffer;

/**
 * 预计有两个实现类，{@link HeapByteBuf} 和 DirectByteBuf。这两个的最大区别在于一种是存在
 * 堆中的数据。另一种是存在内存中的数据。
 *
 * @author fantexi
 * @email netforks@gmail.com
 */
@SuppressWarnings("JavaDoc")
public abstract class ByteBuf implements Serializable {

    final byte[] bs;

    int position = 0;

    int capacity = 0;

    ByteBuf(int capacity) {
        this(capacity, null);
    }

    ByteBuf(int capacity, byte[] bytes) {
        this.capacity = capacity;
        this.bs = new byte[capacity];

        if (bytes != null)
            put(bytes);

    }

    public void put(byte[] bytes) {
        if (bytes.length > capacity)
            throw new ArrayIndexOutOfBoundsException(capacity);

        System.arraycopy(bytes, 0, bs, position, bytes.length);
    }

    /**
     * 复制ByteBuf中的内容
     *
     * @param dest    目标数组
     * @param offset0 开始位置
     * @param offset1 结束位置
     */
    public void copyOf(byte[] dest, int offset0, int offset1) {
        System.arraycopy(bs, offset0,
                dest, offset1,
                offset1 - offset0);
    }

    public byte[] array() {
        return bs;
    }

    public static ByteBuf allocate(int capacity) {
        return allocate(capacity, null);
    }

    /**
     * 分配内存
     *
     * @param capacity 大小
     * @return #ByteBuf
     */
    public static ByteBuf allocate(int capacity, byte[] bytes) {
        return new HeapByteBuf(capacity, bytes);
    }

    /**
     * HeapBuffer
     */
    public static class HeapByteBuf extends ByteBuf {
        HeapByteBuf(int capacity) {
            super(capacity);
        }

        public HeapByteBuf(int capacity, byte[] bytes) {
            super(capacity, bytes);
        }
    }

    /**
     * dui
     */
    public static class DirectByteBuf extends ByteBuf {
        DirectByteBuf(int capacity) {
            super(capacity);
        }
    }

}
