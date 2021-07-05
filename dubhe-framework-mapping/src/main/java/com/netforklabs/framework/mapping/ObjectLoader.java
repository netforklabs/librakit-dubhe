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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.netforklabs.framework.mapping.annotation.DontCareSurvival;
import com.netforklabs.framework.mapping.annotation.Survival;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author fantexi
 * @email netforks@gmail.com
 */
@SuppressWarnings("JavaDoc")
public class ObjectLoader {

    private static final BeanFactory beanFactory = BeanFactory.Companion.get();

    /**
     * 正在初始化但还没初始化完成的内容
     */
    private static final Set<Class<?>> mark                         = Sets.newHashSet();

    private static final Map<Class<?>, List<Field>> skipFields      = Maps.newHashMap();

    public static void forNameArray(List<String> nameArray) throws Exception {
        List<Class<?>> classes = new ArrayList<>(nameArray.size());
        for (String name : nameArray) {
            classes.add(Class.forName(name));
        }

        forClassArray(classes);
    }

    public static void forClassArray(List<Class<?>> classArray) throws Exception {
        for (Class<?> iface : classArray) {
            forClass(iface);

            // 处理被跳过的对象
            skipHandle();
        }
    }

    public static void forClass(Class<?> iface) throws Exception {
        if (skip(iface))
            return;

        // 实例化类
        Object instance = iface.newInstance();

        // 获取类的所有成员, 判断有没有需要注入的成员
        Field[] fields = iface.getDeclaredFields();
        for (Field field : fields) {
            // 注入对象
            field.setAccessible(true);
            if (field.isAnnotationPresent(Survival.class)) {
                Class<?> type = field.getType();
                if (checkEmptyField(field, instance)) {
                    // 如果当前要查找的对象正在初始化, 那么就跳过当前对象
                    if (mark.contains(type)) {
                        if (!skipFields.containsKey(iface))
                            skipFields.put(iface, Lists.newArrayList());

                        skipFields.get(iface).add(field);
                        continue;
                    }

                    // 从Bean容器中查找有没有注入的对象
                    Object typeInstance = beanFactory.get(type);

                    // 没有的话就实例化这个对象
                    if (typeInstance == null) {
                        {
                            // 当前正在实例化对象, 为了防止#type和当前对象重复引用问题。记录到Set集合中
                            mark.add(iface);
                            forClass(type);
                        }
                        typeInstance = beanFactory.get(type);
                        field.set(instance, typeInstance);
                    }
                }
            }
        }

        beanFactory.add(instance, iface);
    }

    private static void skipHandle() throws IllegalAccessException {
        for (Map.Entry<Class<?>, List<Field>> skipEntry : skipFields.entrySet()) {
            Class<?> key = skipEntry.getKey();
            Object object = beanFactory.get(key);

            for (Field field : skipEntry.getValue())
                field.set(object, beanFactory.get(field.getType()));
        }

        mark.clear();
        skipFields.clear();
    }

    /**
     * 跳过反射实例化
     *
     * @param iface 类对象
     */
    private static boolean skip(Class<?> iface) {
        return iface.isInterface()
                || iface.isAnnotationPresent(DontCareSurvival.class)
                || beanFactory.contains(iface);
    }

    /**
     * 检查成员是否为null
     */
    private static boolean checkEmptyField(Field field, Object object) throws IllegalAccessException {
        return field.get(object) == null;
    }

}
