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

package com.netforklabs.netprotocol.message;

import com.netforklabs.netprotocol.Message;
import com.netforklabs.netprotocol.Status;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * @author orval
 * @email orvlas@foxmail.com
 */
@SuppressWarnings("JavaDoc")
public class ErrorMessage extends Message {

    public ErrorMessage() {}

    public ErrorMessage(Throwable e) {

    }

    public ErrorMessage(int status) {
        setStatus(status);
    }

    public ErrorMessage(String msg) {
        buffer = ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public int cmd() {
        return Status.ERROR;
    }

    /**
     * 可以定义一个固定的对象，用于复制。避免每次去new异常信息
     *
     * @param e 异常信息
     * @return 消息对象
     */
    public static ErrorMessage copy(Throwable e) {
        return new ErrorMessage(e);
    }

    public static ErrorMessage copy(int status) {
        return new ErrorMessage(status);
    }

    public static ErrorMessage copy(String msg) {
        return new ErrorMessage(msg);
    }

}
