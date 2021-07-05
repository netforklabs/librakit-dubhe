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

/* Create date: 2021/7/4. */

package com.netforklabs.framework.mapping;

import com.netforklabs.framework.mapping.error.MultipleImplementException
import com.netforklabs.framework.utils.error.ERRORCode
import com.sun.org.apache.xpath.internal.operations.Bool

/**
 * @author fantexi
 * @email netforks@gmail.com
 */
@SuppressWarnings("JavaDoc")
open class BeanFactory {

    /**
     * 所有Bean实例对象
     */
    private val beans: MutableList<Any> = ArrayList()

    private var position: Int = 0
    private val ifaces: MutableMap<Class<*>, Int> = HashMap()

    companion object {
        private var instance: BeanFactory? = null
            get() {
                if (field == null)
                    field = BeanFactory()
                return field
            }

        fun get(): BeanFactory = instance!!

        fun load(nameArray: MutableList<String>) {
            ObjectLoader.forNameArray(nameArray)
        }

        fun load(aClass: Class<*>) {
            ObjectLoader.forClass(aClass)
        }
    }

    /**
     * 添加新的容器对象
     */
    fun add(any: Any, aClass: Class<*>) {
        val interfaces = aClass.interfaces
        addBeanAny(any, aClass)

        for (face in interfaces) {
            // 检测接口是否存在多个实现类
            if (ifaces.containsKey(face)) {
                throw MultipleImplementException(
                    ERRORCode.MULTIPLE_IMPLEMENT_FOR_INTERFACE.get(
                        face.name, "【${beans[ifaces[face]!!].javaClass::getName}】，【${any.javaClass::getName}】"
                    )
                )
            }

            // 如果不存在的话就将接口添加到容器中
            addInterface(face)
        }

        position++
    }

    private fun addBeanAny(any: Any, aClass: Class<*>) {
        beans.add(any)
        addInterface(aClass)
    }

    /**
     * 添加一个接口对象到指向当前索引(#position)
     */
    private fun addInterface(aClass: Class<*>) {
        ifaces[aClass] = position
    }

    /**
     * 获取容器中是否存在指定对象
     */
    fun contains(iface: Class<*>): Boolean = ifaces[iface] != null

    /**
     * 根据Class获取实例对象
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> get(iface: Class<T>): T? {
        val face = ifaces[iface]
        if(face != null) {
            return beans[face] as T
        }

        return null
    }

}