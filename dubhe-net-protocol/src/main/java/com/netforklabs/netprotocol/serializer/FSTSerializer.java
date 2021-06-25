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

/* Create date: 2021/6/23 */

package com.netforklabs.netprotocol.serializer;

import com.netforklabs.netprotocol.message.Message;
import com.netforklabs.utils.bytes.Bytes;
import org.nustaq.serialization.FSTConfiguration;

/**
 * @author orval* @email orvals@foxmail.com
 */
@SuppressWarnings({"JavaDoc", "unchecked"})
public class FSTSerializer implements Serializer {

    private final ThreadLocal<FSTConfiguration> fstcnf
            = ThreadLocal.withInitial(FSTConfiguration::createDefaultConfiguration);

    @Override
    public <T> T decode(byte[] bytes) {
        return (T) fstcnf.get().asObject(bytes);
    }

    @Override
    public byte[] encode(Object object) {
        return fstcnf.get().asByteArray(object);
    }

    @Override
    public byte[] encode(Message object) {
        byte[] serbytes = encode((Object) object);

        int objectSize      = serbytes.length;
        byte[] sizebyte     = Bytes.toByte4(objectSize);
        byte[] buildbyte    = new byte[objectSize + Bytes.INT_BYTE_SIZE]; // 前四个字节表明当前对象的大小

        // 将对象大小填充到最前面
        System.arraycopy(sizebyte, 0, buildbyte, 0, Bytes.INT_BYTE_SIZE);

        // 填充对象内容
        System.arraycopy(serbytes, 0, buildbyte, 4, objectSize);

        return buildbyte;
    }
}
