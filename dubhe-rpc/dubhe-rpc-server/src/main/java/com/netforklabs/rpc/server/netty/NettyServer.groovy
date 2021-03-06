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

package com.netforklabs.rpc.server.netty

import com.netforklabs.rpc.api.DubheServer
import com.netforklabs.rpc.api.DubheServerHandler
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.bytes.ByteArrayDecoder
import io.netty.handler.codec.bytes.ByteArrayEncoder

/**
 * @author luotsforever* @email luotsforevers@foxmail.com
 */
@SuppressWarnings("JavaDoc")
class NettyServer implements DubheServer {

    private var bossGroup = new NioEventLoopGroup()
    private var workerGroup = new NioEventLoopGroup()

    @Override
    void startServer(int port) {
        try {
            var bootstrap = new ServerBootstrap()
            bootstrap.with {
                group(bossGroup, workerGroup)
                channel(NioServerSocketChannel).with {
                    // ?????????????????????????????????
                    option(ChannelOption.SO_BACKLOG, 128)
                    // ????????????????????????
                    childOption(ChannelOption.SO_KEEPALIVE, true)
                    // ?????????????????????
                    childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new ByteArrayDecoder())
                            socketChannel.pipeline().addLast(new ByteArrayEncoder())
                            socketChannel.pipeline().addLast(new NettyServerHandler())
                        }
                    })
                }

                // ????????????
                var channelFuture = bind(port).sync()
                channelFuture.channel().closeFuture().sync()
            }
        } finally {
            // ???????????????
            bossGroup.shutdownGracefully().sync()
            workerGroup.shutdownGracefully().sync()
        }
    }

}
