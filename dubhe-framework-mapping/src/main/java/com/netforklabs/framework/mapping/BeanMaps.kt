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

import com.netforklabs.framework.utils.error.ERRORCode
import java.rmi.NoSuchObjectException

/**
 * @author fantexi
 * @email netforks@gmail.com
 */
@SuppressWarnings("JavaDoc")
class BeanMaps {

    private val beans: MutableList<Any> = ArrayList()

    /**
     * 类名称索引
     */
    private val nameIndices: Map<String, Int> = HashMap()

    /**
     * 接口索引
     */
    private val interfaceIndices: Map<String, Int> = HashMap()

    companion object {
        private var instance: BeanMaps? = null
            get() {
                if (field == null)
                    field = BeanMaps()
                return field
            }

        fun get(): BeanMaps {
            return instance!!
        }
    }

    /**
     * 添加新的容器对象
     */
    fun addObject(any: Any, aClass: Class<*>) {

    }

    /**
     * 根据Class获取实例对象
     */
    fun get(aClass: Class<*>): Any {
        val name: String = aClass.name

        // 如果是接口的话那么从接口索引列表中查找
        if (aClass.isInterface) {
            val index: Int? = interfaceIndices[name]
            checkIndex(index, name)

            return beans[index!!]
        }

        val index: Int? = nameIndices[name]
        checkIndex(index, name)

        return beans[index!!]
    }

    private fun checkIndex(index: Int?, vararg args: String) {
        if (index == null)
            throw NoSuchObjectException(ERRORCode.NO_SUCH_OBJECT_OF_BEAN_MAP.get(*args))
    }

}