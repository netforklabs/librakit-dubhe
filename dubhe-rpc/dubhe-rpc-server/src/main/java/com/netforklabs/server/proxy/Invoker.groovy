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

/* Create date: 2021/6/22 */

package com.netforklabs.server.proxy

import com.netforklabs.config.setting.NetforkSetting
import com.netforklabs.server.Methods

import java.lang.reflect.Method

/**
 * @author orval
 * @email orvlas@foxmail.com
 */
@SuppressWarnings("JavaDoc")
class Invoker {

    /** 函数对象 */
    private Method      method

    /** 调用实例 */
    private Object      instance

    private String methodKey

    /** 上次调用耗时 */
    private long        lastTimeConsuming;

    Invoker() {}

    Invoker(Method method)
    {
        this(method, null)
    }

    /**
     * 构造器
     *
     * @param method    方法实例
     * @param instance  拥有该函数的对象
     */
    Invoker(Method method, Object instance)
    {
        this.method         = method
        this.methodKey      = Methods.getMethodID(method)

        if(!Objects.isNull(instance))
            this.instance   = instance
    }

    /**
     * 执行方法
     * @param args 参数列表
     */
    Object doInvoke(Object... args)
    {
        return doInvoke(instance, args)
    }

    /**
     * 执行方法
     *
     * @param instance 调用实例
     * @param args 参数列表
     */
    @SuppressWarnings('GroovyVariableNotAssigned')
    Object doInvoke(Object instance, Object... args)
    {
        long startTime
        if(NetforkSetting.isDebug())
            startTime = System.currentTimeMillis()

        Object obj = method.invoke(instance, args)

        long endTime
        if(NetforkSetting.isDebug())
            endTime = System.currentTimeMillis()

        if(NetforkSetting.isDebug())
        {
            lastTimeConsuming = endTime - startTime
            println("${method.name}(${methodKey})执行耗时：$lastTimeConsuming")
        }

        return obj
    }

    Method getMethod() { method }

    Object getInstance() { instance }

    String getMethodKey() { methodKey }

    long getLastTimeConsuming() { lastTimeConsuming }
}
