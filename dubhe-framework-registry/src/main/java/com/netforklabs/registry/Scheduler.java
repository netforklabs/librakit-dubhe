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

/* Create date: 2021/6/21 */

package com.netforklabs.registry;

/**
 * 调度器，负责注册中心的调度操作。一个客户端注册后如果注册中心有
 * 多个备份的注册中心，那么这些注册中心就会备份服务到其他注册中心。
 *
 * 其他注册中心数据会进行同步，同步后当前连接的注册中心会将当前可用的注册中心信息每隔N秒进行一次同步。
 * 如果某个注册中心突然挂掉，那么服务会自动切换到其他注册中心去。保证当前服务不会宕机。
 *
 * (English:
 *      Scheduler for registry center. when one server registered to current registry center of config,
 *      if registry center have more registry server, this server will backup other registry center server.
 *
 *      The other registry centers will synchronized data received from main registry center.
 *
 *      If main registry center shutdown for some reason, this server will be based on the synchronized data
 *      to auto choose one registry center connect.
 * )
 *
 * @author orval
 * @email orvlas@foxmail.com
 */
@SuppressWarnings("JavaDoc")
public class Scheduler {



}
