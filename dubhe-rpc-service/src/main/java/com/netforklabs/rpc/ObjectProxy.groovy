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

/* Create date: 2021/6/20 */

package com.netforklabs.rpc

import com.netforklabs.rpc.error.MethodNotFoundException
import com.netforklabs.rpc.error.MismatchedException

import java.lang.reflect.Method

/**
 * 接口代理对象
 *
 * @author orval
 * @email orvlas@foxmail.com
 */
@SuppressWarnings("JavaDoc")
class ObjectProxy {

    /**
     * 代理的接口类
     */
    private Class<?>                iface

    /**
     * 代理类实现对象
     */
    private Object                  object

    /**
     * 代理类函数列表
     * (English:
     *      function list for proxy class.
     * )
     */
    private Map<String, Method>     methods = [:]

    private ObjectProxy(Class<?> iface, Object object)
    {
        this.iface      = iface
        this.object     = object
    }

    void putMethods(Map<String, Method> methodMap)
    {
        if(methodMap == null)
            return

        this.methods.putAll(methodMap)
    }

    /**
     * 接口代理类
     * ( English:
     *      proxy class of interface.
     * )
     *
     * @param iface 接口类对象
     * @param object 接口实现类对象
     * @return 代理类对象
     */
    static ObjectProxy createProxyObject(Class<?> iface, Object object)
    {
        if(iface == null)
            throw new NullPointerException("param[iface] is null.")

        if(iface == null)
            throw new NullPointerException("param[object] is null.")

        checkObjectIsImplFace(iface, object)

        ObjectProxy proxy = new ObjectProxy(iface, object)
        proxy.putMethods(parseMethod(iface))

        return proxy
    }

    /**
     * 解析方法
     */
    static Map<String, Method> parseMethod(Class<?> iface)
    {
        Map<String, Method> methods = new HashMap<>()
        iface.declaredMethods.each {method ->
            methods.put(Methods.getMethodID(method), method)
        }

        return methods
    }

    /**
     * 判断实现类是否继承了接口
     *
     * @param iface  接口类对象
     * @param object 接口实现类对象
     */
    private static void checkObjectIsImplFace(Class<?> iface,
                                              Object   object)
    {

        // 判断 object 是否实现了 iface 接口
        boolean isTrue = false

        Class<?> objectClass = object.class
        for(Class<?> face : objectClass.interfaces)
        {
            if (face.name == iface.name) {
                isTrue = true
                break
            }
        }

        if(!isTrue)
            throw new MismatchedException("interface: ${iface.name} and object: ${object.class.name} not matched.")
    }

    /**
     * 执行代理类的函数。(返回值：Object)
     * ( English:
     *      Execute function of proxy class. (return value: Object)
     * )
     *
     * @param name 函数名
     * @param args 参数
     * @return 函数返回值
     */
    Object invoke(String name, Object... args)
    {
        if(!methods.containsKey(name))
            throw new MethodNotFoundException("${name} method key not found.")

        return methods[name].invoke(object, args)
    }

}
