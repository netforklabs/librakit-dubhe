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

package com.netforklabs.rpc.api;

import com.netforklabs.net.protocol.message.Message;

/**
 * @author luotsforever
 * @email orvlas@foxmail.com
 */
@SuppressWarnings("JavaDoc")
public interface DubheChannel {

    /**
     * @return Channel id
     */
    String id();

    /**
     * 【写事件】将数据发送到客户端
     * @param bytes 字节数组
     */
    void send(byte[] bytes);

    /**
     * 【写事件】将数据发送到客户端
     * @param header 协议
     */
    void send(Message header);

    /**
     * 刷新
     */
    void flush();

    /**
     * 断开连接
     */
    void disconnect();

    /**
     * 关闭连接
     */
    void close();

    <T> T getRealChannel();

}
