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

package com.netforklabs.dubhe;

import com.netforklabs.framework.config.setting.NetforkSetting;
import com.netforklabs.framework.mapping.proxy.ObjectProxy;

import java.util.Set;

/**
 * @author orval
 * @email orvlas@foxmail.com
 */
@SuppressWarnings("JavaDoc")
public class UserServiceProxyTest {

    public static void main(String[] args) {

        NetforkSetting.compile();

        ObjectProxy proxyObject = ObjectProxy.createProxyObject(UserService.class, new UserServiceImpl());

        Set<String> names = proxyObject.getNames();
        for(String v : names)
            System.out.println(v);

        User user = (User) proxyObject.doInvoke("findUser&java.lang.String", "张三");
        System.out.println(user.toString());

    }

}
