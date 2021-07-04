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

import com.netforklabs.rpc.api.DubheChannel
import com.netforklabs.rpc.api.DubheClient
import io.netty.bootstrap.Bootstrap
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelInitializer
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.bytes.ByteArrayDecoder
import io.netty.handler.codec.bytes.ByteArrayEncoder

/**
 * @author luotsforever* @email luotsforevers@foxmail.com
 */
@SuppressWarnings("JavaDoc")
class NettyClient implements DubheClient {

    private static var eventGroup = new NioEventLoopGroup()

    private static final Map<String, DubheChannel> channels = [:]

    /**
     * 初始化Bootstrap
     */
    private static Bootstrap createBootstrap() {
        var bootstrap = new Bootstrap()
        bootstrap.with {
            group(eventGroup)
            channel(NioSocketChannel).with {
                handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline().addLast(new ByteArrayEncoder())
                        channel.pipeline().addLast(new ByteArrayDecoder())
                        channel.pipeline().addLast(new NettyClientHandler())
                    }
                })
            }
        }

        return bootstrap
    }

    @Override
    DubheChannel connect(String host, int port) {
        var bootstrap = createBootstrap()

        ChannelFuture future = bootstrap.connect(host, port)

        // 连接建立好后回调
        future.addListener(new ChannelFutureListener() {
            @Override
            void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (!channelFuture.isSuccess())
                    throw new ConnectException("连接到 - ${host}:${port} 失败")
            }
        })

        var syncChannel = future.sync().channel()
        NettyChannel channel = new NettyChannel(syncChannel)
        channels.put(channel.id(), channel)

        return channel
    }

    @Override
    DubheChannel getChannel(String id) {
        return channels.get(id)
    }

}
