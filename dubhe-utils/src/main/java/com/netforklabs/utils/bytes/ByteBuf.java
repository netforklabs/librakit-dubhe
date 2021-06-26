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
import java.util.ArrayList;

/**
 * 预计有两个实现类，{@link HeapByteBuf} 和 DirectByteBuf。这两个的最大区别在于一种是存在
 * 堆中的数据。另一种是存在内存中的数据。
 *
 * @author fantexi
 * @email netforks@gmail.com
 */
@SuppressWarnings("JavaDoc")
public abstract class ByteBuf implements Serializable {

    byte[] array;

    int position = 0;

    int capacity = 0;

    public static final int AUTO_CAPACITY = -1;

    ByteBuf(int capacity) {
        this(capacity, null);
    }

    ByteBuf(int capacity, byte[] bytes) {
        this.capacity = capacity;
        this.array = new byte[capacity];

        if (bytes != null)
            put(bytes);

    }

    public int size() {
        return capacity;
    }

    public void put(byte b) {
        position++;
        check(1, position, capacity);
        array[position] = b;
    }

    public void put(byte[] bytes) {
        check(bytes.length, position, capacity);
        uncheckedPut(bytes);
    }

    void uncheckedPut(byte[] bytes) {
        int length = bytes.length;
        System.arraycopy(bytes, 0, array, position, length);
        position += length;
    }

    // 检查数组下标是否越界
    private static void check(int size, int pos, int capacity) {
        if (size > (capacity - pos - 1))
            throw new ArrayIndexOutOfBoundsException(capacity);
    }

    //
    // 最终数组
    //
    public abstract void arrayFinal();

    /**
     * 复制ByteBuf中的内容
     *
     * @param dest    目标数组
     * @param offset0 开始位置
     * @param offset1 结束位置
     */
    public void copyOf(byte[] dest, int offset0, int offset1) {
        System.arraycopy(array, offset0,
                dest, offset1,
                offset1 - offset0);
    }

    public byte[] array() {
        return array;
    }

    //
    // 自动分配内存
    //
    public static ByteBuf autoAllocate() {
        return new AutoAllocateByteBuf(16);
    }

    //
    // 根据大小分配内存
    //
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
        if (capacity == AUTO_CAPACITY)
            return autoAllocate();

        return new HeapByteBuf(capacity, bytes);
    }

    /**
     * 堆内存缓冲区
     */
    public static class HeapByteBuf extends ByteBuf {
        HeapByteBuf(int capacity) {
            super(capacity);
        }

        public HeapByteBuf(int capacity, byte[] bytes) {
            super(capacity, bytes);
        }

        @Override
        public void arrayFinal() {
        }
    }

    /**
     * 堆外内存缓冲区
     */
    public static class DirectByteBuf extends ByteBuf {
        DirectByteBuf(int capacity) {
            super(capacity);
        }

        @Override
        public void arrayFinal() {
        }
    }

    //
    // 自动分配内存（堆内存缓冲区）
    //
    public static class AutoAllocateByteBuf extends HeapByteBuf {

        private boolean __final = false;

        public AutoAllocateByteBuf(int capacity) {
            super(capacity);
        }

        public AutoAllocateByteBuf(int capacity, byte[] bytes) {
            super(capacity, bytes);
        }

        @Override
        public void put(byte b) {
            if(__final)
                return;

            // 确保内部容量够当前字节数
            ensureCapacityInternal(1);
            super.put(b);
        }

        @Override
        public void put(byte[] bytes) {
            if(__final || bytes == null)
                return;

            // 确保内部容量够当前字节数
            ensureCapacityInternal(bytes.length);
            super.uncheckedPut(bytes);
        }

        // 停止数组扩容
        public void arrayFinal() {
            if (position != capacity) {
                final byte[] finalArray = new byte[position];
                System.arraycopy(array, 0, finalArray, 0, position);
                this.array = finalArray;
                this.capacity = finalArray.length;
            }

            __final = true;
        }

        //
        // 确保内部容量足够
        //
        private void ensureCapacityInternal(int size) {
            if ((capacity - position - 1) < size) {
                int newCapacity;
                if (size > 32) {
                    newCapacity = capacity + size;
                } else {
                    newCapacity = capacity + 32;
                }

                byte[] nByteArray = new byte[newCapacity];
                System.arraycopy(this.array, 0, nByteArray, 0, capacity);

                this.array = nByteArray;
                this.capacity = newCapacity;
            }
        }

    }

}
