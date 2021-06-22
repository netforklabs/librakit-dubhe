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

import java.nio.file.Files

/**
 * @author orval
 * @email orvlas@foxmail.com
 */
@Getter
@Setter
@SuppressWarnings("JavaDoc")
class NetforkSetting {

    /** 注册中心地址 */
    private List<Registry>          registries       = []

    /** 远程调用的服务信息 */
    private List<Server>            servers          = []

    /** 配置信息 */
    private Map<String, String>     settings         = [:]

    static final String             VARIABLE_NAME    = "netfork"

    /**
     * 是否开启调试
     */
    private boolean debug = false

    private NetforkSetting() {}

    /**
     * 单例对象
     */
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
        binding.setVariable(VARIABLE_NAME, getNetforkSetting())

        GroovyShell shell = new GroovyShell(binding)
        shell.evaluate(getScriptText())
    }

    /**
     * 解析配置脚本
     */
    private static String getScriptText()
    {
        StringBuilder scriptBuilder = new StringBuilder()

        scriptBuilder.append """
            import com.netforklabs.config.setting.Registry
            import com.netforklabs.config.setting.Server
        \n"""

        Class<?> aClass = NetforkSetting.class
        aClass.declaredMethods.each {method->
            if(method.isAnnotationPresent(Alias))
            {
                Alias alias = method.getDeclaredAnnotation(Alias)
                scriptBuilder.append alias.value().replace("VARIABLE_NAME", VARIABLE_NAME) + "\n"
            }
        }

        URL settingUrl          = NetforkSetting.classLoader.getResource("./main.netfork")
        File settingFile        = new File(settingUrl.path)
        byte[] bytes            = Files.readAllBytes(settingFile.toPath())

        if(bytes.length > 0)
            scriptBuilder.append(new String(bytes))

        return scriptBuilder.toString()
    }

    /**
     * 配置当前服务信息，两个key。
     *
     * @param hostname 当前服务名称，这个名称会被注册中心使用的。
     * @param port     当前服务端口号
     */
    @Alias("void server(Map<String, String> serverInfo) { VARIABLE_NAME.addServer(serverInfo) }")
    void addServer(Map<String, String> serverInfo) { servers.add(new Server(serverInfo.hostname, serverInfo.port)) }

    /**
     * @return 配置的所有PRC远程服务信息
     */
    @Alias("List<Server> server() { VARIABLE_NAME.getServers() }")
    List<Server> getServers() { this.servers }

    /**
     * @return 获取当前是否开启调试
     */
    @Alias("boolean debug() { VARIABLE_NAME.isDebug() }")
    boolean isDebug() { this.debug }

    /**
     * 设置调试模式
     * @param value true开启，false关闭
     */
    @Alias("void debug(boolean value) { VARIABLE_NAME.setDebug(value) }")
    void setDebug(boolean value) { this.debug = value }

    /**
     * 配置注册中心
     * @param regmap 注册中心配置属性Map
     */
    @Alias("void registries(Map<String, String> regmap) { VARIABLE_NAME.addRegistry(regmap) }")
    void addRegistry(Map<String, String> regmap) { registries.add(new Registry(regmap.host, regmap.pass)) }

    /**
     * 获取当前配置的所有注册中心
     */
    @Alias("List<Registry> registries() { VARIABLE_NAME.registries }")
    List<Registry> getRegistries() { registries }

    /**
     * 配置当前服务名称
     */
    @Alias("void servername(String name) { VARIABLE_NAME.setServerName(name) }")
    void setServerName(String name) { settings.put("servername", name) }

    /**
     * @return 当前配置的服务名称
     */
    @Alias("String servername() { VARIABLE_NAME.getServerName() }")
    String getServerName() { settings.servername }

}
