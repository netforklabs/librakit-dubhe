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

/* Create date: 2021/7/3. */

package com.netforklabs.framework.utils.error;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author fantexi
 * @email netforks@gmail.com
 */
@SuppressWarnings("JavaDoc")
public class InchainLang {

    public static final Map<ERRORCode, String> MESSAGE = Maps.newHashMap();

    static {
        MESSAGE.put(ERRORCode.CLASSPATH_NOT_FOUND_OR_CLASSPATH_HAS_NO_CONTENT, "类路径不存在或路径下没有文件内容, 类路径: {}。");
        MESSAGE.put(ERRORCode.NO_SUCH_OBJECT_OF_BEAN_MAP, "找不到对象实例: {}");
        MESSAGE.put(ERRORCode.MULTIPLE_IMPLEMENT_FOR_INTERFACE, "{}接口含有多个实现，实现类列表：{}");
    }

}
