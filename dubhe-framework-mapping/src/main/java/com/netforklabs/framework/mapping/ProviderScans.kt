@file:Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.netforklabs.framework.mapping

import com.netforklabs.framework.mapping.bean.BeanFactory
import com.netforklabs.framework.mapping.error.ClassPathNotFoundException
import com.netforklabs.framework.utils.StringUtils
import com.netforklabs.framework.utils.error.ERRORCode
import java.io.File
import java.net.URL

/**
 * @author luotsforever
 * @email orvlas@foxmail.com
 */
@SuppressWarnings("JavaDoc")
object ProviderScans {

    private val classLoader: ClassLoader               = ProviderScans.javaClass.classLoader
    private val classRoot:         Int                 = classLoader.getResource("").file.length - 1
    private const val classExtend: Int                 = 6 // ".class".length

    @JvmStatic fun doScan(classpath: String) {
        val resource: URL = classLoader.getResource(classpath.replace(".", "/"))
                ?: return except(ERRORCode.CLASSPATH_NOT_FOUND_OR_CLASSPATH_HAS_NO_CONTENT.get(), classpath)
        val sile          = resource.file

        val classfiles: MutableList<String> = ArrayList()
        doScan(File(sile), classfiles)

        classfiles.forEach { classfile->
            BeanFactory.build(classfile)
        }
    }

    /**
     * 扫描包下的Class文件, 并实例化
     */
    @JvmStatic fun doScan(classfile: File, classfiles: MutableList<String>) {
        classfile.listFiles()?.forEach { file ->
            if(file.isDirectory) {
                doScan(file, classfiles)
            } else {
                var cated: String = catClassPath(file.absolutePath)
                cated = if(cated.contains("\\"))
                            cated.replace("\\", ".")
                        else
                            cated.replace("", ".")

                classfiles.add(cated)
            }
        }
    }

    @JvmStatic fun catClassPath(classpath: String): String = classpath.substring(classRoot, (classpath.length - classExtend))

    // 抛出异常
    @JvmStatic private fun except(msg: String, vararg args: String) {
        throw ClassPathNotFoundException(StringUtils.format(msg, *args))
    }

}