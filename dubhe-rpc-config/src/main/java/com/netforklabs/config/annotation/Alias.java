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

package com.netforklabs.config.annotation;

import java.lang.annotation.*;

/**
 * @author orval
 * @email orvlas@foxmail.com
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
@SuppressWarnings("JavaDoc")
public @interface Alias {

    /**
     * 使用别名必须保证配置属性名的唯一性，否则检测时会报错。
     *
     * 比如配置对象 A 使用别名 a, B 使用别名 b。他们有共同的属性叫 c，
     * 这时候检测到两个相同配置类属性名不唯一就会抛出异常。
     *
     * ( English:
     *      When using @Alias annotation, The attribute name must be unique.
     *      Example: now we have two config object, are A object and B object respectively.
     *      they have common property called 'c', at this moment the startup check will be throws
     *      {@link java.text.ParseException}.
     * )
     *
     * @return 别名名称
     */
    String value() default "";

}
