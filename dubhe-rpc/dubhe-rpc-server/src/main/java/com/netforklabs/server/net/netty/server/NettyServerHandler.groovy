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

import com.netforklabs.api.DubheClient
import com.netforklabs.api.DubheServerHandler
import com.netforklabs.api.event.AddEvent
import com.netforklabs.api.event.EventHandler
import com.netforklabs.api.event.ReadableEventHandler
import io.netty.channel.ChannelHandlerAdapter
import io.netty.channel.ChannelHandlerContext

/**
 * @author orval
 * @email orvals@foxmail.com
 */
@SuppressWarnings("JavaDoc")
class NettyServerHandler extends ChannelHandlerAdapter
        implements DubheServerHandler, AddEvent {

    private ReadableEventHandler readable

    @Override
    void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        readable.read(null, null)
    }

    @Override
    void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx)
    }

    @Override
    void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace()
    }

    @Override
    void disconnect(DubheClient client) {

    }

    @Override
    void read(DubheClient client, Object msg) {

    }

    @Override
    void addEvent(EventHandler event) {
        if(event instanceof ReadableEventHandler)
            this.readable = event as ReadableEventHandler
    }

}
