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

package com.netforklabs.net.protocol.message;

import com.netforklabs.framework.config.setting.NetforkSetting;

import java.io.Serializable;

/**
 * 通讯协议，所有网络传输对象都必须继承该对象下。可以理解为
 * 协议请求头。
 *
 * @author orval
 * @email orvlas@foxmail.com
 */
@SuppressWarnings("JavaDoc")
public abstract class Message implements Serializable {

    public static final int FST_SERIALIZER          = 0;
    public static final int PROTOCOL_VERSION        = charAdd('C', '1');

    /**
     * 魔数: 取值范围十六进制 0123456789 ABCDEF
     */
    private int magicNumber = NetforkSetting.MAGIC;

    /**
     * 序列化算法
     */
    private int serializer = FST_SERIALIZER;

    /**
     * 为了防止协议升级，当前协议版本
     */
    private int version = PROTOCOL_VERSION;

    /**
     * 请求状态：例如发起一次RPC请求成功或者是失败。
     */
    protected int status;

    public Message() {}

    @SuppressWarnings("CopyConstructorMissesField")
    public Message(Message msg)
    {
        copyOf(msg);
    }

    /**
     * 需要子类自己去实现它。
     *
     * @return 返回指令类型
     */
    public abstract int cmd();

    /**
     * 拷贝一些基本数据到当前类
     * @param msg 协议头
     */
    public void copyOf(Message msg)
    {
        if(msg != null)
        {
            this.magicNumber    = msg.getMagicNumber();
            this.serializer     = msg.getSerializer();
            this.version        = msg.getVersion();
        }
    }

    public int getMagicNumber() {
        return magicNumber;
    }

    public void setMagicNumber(int magicNumber) {
        this.magicNumber = magicNumber;
    }

    public int getSerializer() {
        return serializer;
    }

    public void setSerializer(byte serializer) {
        this.serializer = serializer;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    private static int charAdd(char x, char y)
    {
        return ((byte) x) + ((byte) y);
    }

}
