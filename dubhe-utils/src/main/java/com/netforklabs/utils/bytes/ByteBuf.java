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
import java.util.Arrays;

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

    // 默认扩容的字节数
    private static final int AUTO_ALLOCATE_CAPACITY = 32;

    ByteBuf(int capacity) {
        this(capacity, null);
    }

    ByteBuf(int capacity, byte[] bytes) {
        this.capacity = capacity;
        this.array = new byte[capacity];

        if (bytes != null)
            put(bytes);

    }

    public int asInt() {
        return Bytes.toInt(array);
    }

    public int asInt(int from) {
        return Bytes.toInt(array, from);
    }

    /**
     * 获取已用空间
     */
    public int size() {
        return position;
    }

    /**
     * 当前ByteBuf是否为空
     */
    public boolean isEmpty() {
        return position == 0;
    }

    public void put(byte b) {
        check(1, position, capacity);
        array[position] = b;
        position++;
    }

    public void put(byte[] bytes) {
        check(bytes.length, position, capacity);
        uncheckedPut(bytes);
    }

    public byte[] copyOf(int from) {
        return copyOf(from, capacity);
    }

    public byte[] copyOf(int from, int to) {
        return Arrays.copyOfRange(array, from, to);
    }

    void uncheckedPut(byte[] bytes) {
        int length = bytes.length;
        System.arraycopy(bytes, 0, array, position, length);
        position += length;
    }

    // 检查数组下标是否越界
    private static void check(int size, int pos, int capacity) {
        if (size > (capacity - pos))
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

    // 将byte内容转int, 值从from往后推3位。共4位。
    public int getInt(int from) {
        return Bytes.toInt(array, from);
    }

    //
    // 清空当前byte数组
    //
    public ByteBuf clear() {
        position = 0;
        array = new byte[capacity];
        return this;
    }

    //
    // 自动分配内存
    //
    public static ByteBuf autoAllocate() {
        return new AutoAllocateByteBuf(AUTO_ALLOCATE_CAPACITY);
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

        // 最大调用次数
        static final int MAX_FREQUENCY = 5;

        // 如果多次扩容大小大于默认扩容大小，那么当达到一定峰值
        // 后就增加默认扩容大小 #ENSURE_CAPACITY_INTERNAL_SIZE
        private int frequency = 0;

        private int ENSURE_CAPACITY_INTERNAL_SIZE = AUTO_ALLOCATE_CAPACITY;

        public AutoAllocateByteBuf(int capacity) {
            super(capacity);
        }

        public AutoAllocateByteBuf(int capacity, byte[] bytes) {
            super(capacity, bytes);
        }

        @Override
        public void put(byte b) {
            if (__final)
                return;

            // 确保内部容量够当前字节数
            ensureCapacityInternal(1);
            super.put(b);
        }

        @Override
        public void put(byte[] bytes) {
            if (__final || bytes == null)
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

        @Override
        public ByteBuf clear() {
            capacity = AUTO_ALLOCATE_CAPACITY;
            return super.clear();
        }

        //
        // 确保内部容量足够
        //
        private void ensureCapacityInternal(int size) {
            if ((capacity - position - 1) < size) {
                int newCapacity;
                if (size > ENSURE_CAPACITY_INTERNAL_SIZE) {
                    newCapacity = capacity + size;

                    // 扩容大小超过默认分配大小, frequency + 1
                    frequency++;
                    if (frequency >= MAX_FREQUENCY) {
                        ENSURE_CAPACITY_INTERNAL_SIZE = ENSURE_CAPACITY_INTERNAL_SIZE * 2;
                        frequency = 0;
                    }
                } else {
                    newCapacity = capacity + ENSURE_CAPACITY_INTERNAL_SIZE;

                    // 如果当前扩容大小不超过默认扩若大小, 并且frequency != 1
                    if (frequency > 0) {
                        frequency--;
                        if (frequency == 0)
                            ENSURE_CAPACITY_INTERNAL_SIZE = AUTO_ALLOCATE_CAPACITY;
                    }
                }

                byte[] nByteArray = new byte[newCapacity];
                System.arraycopy(this.array, 0, nByteArray, 0, capacity);

                this.array = nByteArray;
                this.capacity = newCapacity;
            }
        }

    }

}
