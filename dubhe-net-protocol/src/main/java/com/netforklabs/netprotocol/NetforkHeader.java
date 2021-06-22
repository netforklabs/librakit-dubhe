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

package com.netforklabs.netprotocol;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 通讯协议，所有网络传输对象都必须继承该对象下。可以理解为
 * 协议请求头。
 *
 * @author orval
 * @email orvlas@foxmail.com
 */
@Getter
@Setter
@SuppressWarnings("JavaDoc")
public abstract class NetforkHeader implements Serializable {

    /**
     * 验证头，防止被意外调用
     */
    private int magicNumber;

    /**
     * 序列化算法
     */
    private byte serialize;

    /**
     * 为了防止协议升级，当前协议版本
     */
    private int version;

    /**
     * 调用指令
     */
    private byte command;

    /**
     * 请求状态：例如发起一次RPC请求成功或者是失败。
     */
    private int status;

    /**
     * 请求大小
     */
    private int size;

    public NetforkHeader() {}

    public NetforkHeader(NetforkHeader netforkHeader)
    {
        copyOf(netforkHeader);
    }

    /**
     * 需要子类自己去实现它。
     *
     * @return 返回指令类型
     */
    public abstract byte getCommand();

    /**
     * 拷贝一些基本数据到当前类
     * @param netforkHeader 协议头
     */
    public void copyOf(NetforkHeader netforkHeader)
    {
        if(netforkHeader != null)
        {
            this.magicNumber    = netforkHeader.getMagicNumber();
            this.serialize      = netforkHeader.getSerialize();
            this.version        = netforkHeader.getVersion();
        }
    }

}
