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

/* Create date: 2021/6/21 */

package com.netforklabs.net.protocol;

/**
 * 指令定义
 *
 * @author luotsforever
 * @email orvlas@foxmail.com
 */
@SuppressWarnings("JavaDoc")
public interface Commands {

    /** 第一次打招呼 */
    int HELLO                   = 0xC4F0;

    /**
     * RPC远程函数调用
     */
    int INVOKE                  = 0xC4F1;

    /**
     * 将服务注册到注册中心
     */
    int APPLY_FOR_REG           = 0xC4F2;

    /**
     * 申请链接
     */
    int CONNECT                 = 0xC4F3;

    /**
     * 断开链接
     */
    int DISCONNECT              = 0xC4F4;

    /**
     * 心跳包
     */
    int HEART_BEAT              = 0xC4F5;

    /**
     * 数据传输包大小
     */
    int SIZE                    = 0xC4F6;

    /**
     * 调用函数返回
     */
    int INVOKE_RETURN           = 0xC4F7;

}
