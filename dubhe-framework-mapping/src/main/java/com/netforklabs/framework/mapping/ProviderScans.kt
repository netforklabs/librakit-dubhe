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

@file:Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.netforklabs.framework.mapping;
import com.netforklabs.framework.utils.StringUtils
import java.io.File

/**
 * @author fantexi
 * @email netforks@gmail.com
 */
@SuppressWarnings("JavaDoc")
object ProviderScans {

    const     val classextend: Int              = 6 // .class
    @JvmField val classloader: ClassLoader      = ProviderScans.javaClass.classLoader
    @JvmField val calssroot  : Int              = classloader.getResource("").file.length - 1

    /**
     * 扫描指定路径下的所有类, 并加载到Bean容器中。
     */
    @JvmStatic fun doScan(classpath: String)
    {
        if(StringUtils.isEmpty(classpath))
            return

        val classfile = classloader.getResource(classpath.replace(".", "/")).file
        val files: MutableList<String> = ArrayList()

        doScanForClass(File(classfile), files)
    }

    // 扫描加载路径下的所有.class文件
    @JvmStatic fun doScanForClass(classfile: File, classfiles: MutableList<String>)
    {
        classfile.listFiles().forEach { file ->
            if(file.isDirectory)
            {
                doScanForClass(file, classfiles)
            } else
            {
                val name = file.absolutePath
                val end  = name.length - classextend

                // 添加类路径
                if(name.substring(end, name.length) == ".class")
                    classfiles.add(name.substring(calssroot, end))
            }
        }
    }

    /**
     * 将 \ 或者 / 替换为 .
     */
    @JvmStatic private fun replaceSymbol(classpath: String): String
    {
        var classpath0 = classpath;

        if(classpath0.contains("\\")) {
            classpath0 = classpath0.replace("\\", ".")
        }

        if(classpath0.contains("/")) {
            classpath0 = classpath0.replace("/", ".")
        }

        return classpath0
    }

}