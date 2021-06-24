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

/* Create date: 2021/6/23 */

package com.netforklabs.server.netty;

import com.netforklabs.api.DubheChannel;
import com.netforklabs.netprotocol.Message;
import com.netforklabs.netprotocol.decoder.Serializer;
import com.netforklabs.netprotocol.decoder.SerializerFactory;
import com.netforklabs.netprotocol.message.ErrorMessage;
import com.netforklabs.server.Channels;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

/**
 * @author orval
 * @email orvals@foxmail.com
 */
@SuppressWarnings({"JavaDoc", "unchecked"})
public class NettyChannel implements DubheChannel {

    private Channel channel;


    private static final Serializer serializer = SerializerFactory.getSerializer();

    public NettyChannel() {
    }

    public NettyChannel(Channel channel) {
        this.channel = channel;
    }

    @Override
    public String id() {
        return Channels.getChannelId(channel);
    }

    @Override
    public void send(byte[] byteArray) {
        ByteBuf buffer = Unpooled.buffer(byteArray.length);
        buffer.writeBytes(byteArray);

        channel.writeAndFlush(buffer);
    }

    @Override
    public void send(Message header) {
        send(serializer.encode(header));
    }

    @Override
    public void error(Throwable error) {
        send(ErrorMessage.copy(error));
    }

    @Override
    public void error(int status) {
        send(ErrorMessage.copy(status));
    }

    @Override
    public void error(String msg) {
        send(ErrorMessage.copy(msg));
    }

    @Override
    public <T> T getRealChannel() {
        return (T) channel;
    }
}
