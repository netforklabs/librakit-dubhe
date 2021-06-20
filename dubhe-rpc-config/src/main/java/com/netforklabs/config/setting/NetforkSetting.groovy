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

package com.netforklabs.config.setting

import com.netforklabs.config.annotation.Alias
import lombok.Getter
import lombok.Setter

/**
 * @author orval
 * @email orvlas@foxmail.com
 */
@Getter
@Setter
@Alias("service")
@SuppressWarnings("JavaDoc")
class NetforkSetting {

    /**
     * 注册中心地址
     */
    private List<Registry> registries = []

    /**
     * 配置信息
     */
    private Map<String, String> settings = ["a": "1"]

    private static NetforkSetting netforkSetting

    static NetforkSetting getNetforkSetting()
    {
        if(!netforkSetting)
            netforkSetting = new NetforkSetting()

        return netforkSetting
    }

    /**
     * 编译配置文件运行
     */
    static void compile()
    {
        Binding binding = new Binding()
        binding.setVariable("netfork", getNetforkSetting())

        GroovyShell shell = new GroovyShell(binding)
        shell.evaluate(getNetforkFileURL().toURI())
    }

    private static URL getNetforkFileURL()
    {
        return NetforkSetting.classLoader.getResource("./main.netfork")
    }

    /**
     * 配置注册中心
     * @param regmap 注册中心配置属性Map
     */
    void registry(Map<String, String> regmap)
    {
        registries.add(new Registry(regmap.host, regmap.pass))
    }

    /**
     * 配置当前服务信息，两个key。
     *
     * @param hostname 当前服务名称，这个名称会被注册中心使用的。
     * @param port     当前服务端口号
     */
    void serinfo(Map<String, String> serverInfo)
    {
        settings.putAll(serverInfo)
    }

}
