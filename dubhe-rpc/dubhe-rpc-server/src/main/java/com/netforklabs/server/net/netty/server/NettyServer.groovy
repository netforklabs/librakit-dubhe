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

/* Create date: 2021/6/22 */

package com.netforklabs.server.net.netty.server

import com.netforklabs.api.DubheServer
import com.netforklabs.api.DubheServerHandler
import com.netforklabs.api.event.EventHandler
import com.netforklabs.api.event.ReadableEventHandler
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel

/**
 * @author orval* @email orvals@foxmail.com
 */
@SuppressWarnings("JavaDoc")
class NettyServer implements DubheServer {

    private var bossGroup          = new NioEventLoopGroup()
    private var workerGroup        = new NioEventLoopGroup()
    private var serverHandler      = new NettyServerHandler()

    @Override
    void startServer(int port) {
        try {
            var bootstrap = new ServerBootstrap()
            bootstrap.with {
                group(bossGroup, workerGroup)
                channel(NioServerSocketChannel).with {
                    // 线程队列得到的链接个数
                    option(ChannelOption.SO_BACKLOG, 128)
                    // 保持活动链接状态
                    childOption(ChannelOption.SO_KEEPALIVE, true)
                    // 初始化通道对象
                    childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(serverHandler)
                        }
                    })
                }

                // 绑定端口
                var channelFuture = bind(port).sync()
                channelFuture.channel().closeFuture().sync()
            }
        } finally {
            // 关闭线程组
            bossGroup.shutdownGracefully()
            workerGroup.shutdownGracefully()
        }
    }

    @Override
    void addEvent(EventHandler event) {
        serverHandler.addEvent(event)
    }

    @Override
    DubheServerHandler getHandler() { serverHandler }
}
