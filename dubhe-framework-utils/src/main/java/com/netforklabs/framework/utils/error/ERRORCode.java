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

import com.netforklabs.framework.utils.StringUtils;

import java.util.Map;

/**
 * @author fantexi
 * @email netforks@gmail.com
 */
@SuppressWarnings("JavaDoc")
public enum ERRORCode {

    /**
     * 原因: $format路径不存在或路径下没有文件内容。
     */
    CLASSPATH_NOT_FOUND_OR_CLASSPATH_HAS_NO_CONTENT,

    /**
     * 原因: 找不到需要注入的对象实例。
     */
    NO_SUCH_OBJECT_OF_BEAN_MAP,

    /**
     * 接口含有多个实现
     */
    MULTIPLE_IMPLEMENT_FOR_INTERFACE,

    NONE;

    private static final Map<ERRORCode, String> errors = InchainLang.MESSAGE;

    public String get() {
        return errors.get(this);
    }

    public String get(Object... args) {
        return StringUtils.format(errors.get(this), args);
    }


}
