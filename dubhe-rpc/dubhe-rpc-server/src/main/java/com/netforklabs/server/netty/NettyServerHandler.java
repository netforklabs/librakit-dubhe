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

package com.netforklabs.server.netty;

import com.netforklabs.api.DubheChannel;
import com.netforklabs.api.DubheClient;
import com.netforklabs.api.DubheServerHandler;
import com.netforklabs.config.setting.NetforkSetting;
import com.netforklabs.netprotocol.message.Message;
import com.netforklabs.netprotocol.serializer.Serializer;
import com.netforklabs.netprotocol.serializer.SerializerFactory;
import com.netforklabs.server.Channels;
import com.netforklabs.utils.bytes.Bytes;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author orval
 * @email orvals@foxmail.com
 */
@SuppressWarnings("JavaDoc")
public class NettyServerHandler extends ChannelHandlerAdapter
        implements DubheServerHandler {

    private final Map<String, DubheChannel> channels = new HashMap<>();

    private static final Serializer serializer = SerializerFactory.getSerializer();

    /**
     * 拆包
     *
     * @param byteArray 字节数组
     */
    private static List<byte[]> unpack(byte[] byteArray) {
        List<byte[]> objectBuffers = new ArrayList<>();

        int position = 0;
        while (position < byteArray.length) {
            int offset = Bytes.toInt(byteArray, position) + Bytes.INT_BYTE_SIZE;
            position += 4;

            // 这是一个完整的字节数组对象
            byte[] object = new byte[offset - Bytes.INT_BYTE_SIZE];

            // 数组复制
            System.arraycopy(byteArray, position, object, 0, object.length);
            objectBuffers.add(object);

            position += object.length;
        }

        return objectBuffers;
    }

    /**
     * 是否需要进行拆包，如果前四个字节的整型数组等于byteArray的大小，就代表不需要
     * 拆包当前只有一个对象上传。如果大于就代表需要进行拆包操作。
     *
     * @param byteArray 字节数组
     * @return true | false
     */
    private static boolean checkUnpack(byte[] byteArray) {
        return Bytes.toInt(byteArray, 0) == (byteArray.length - 4);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        // 建立一个新的连接
        NettyChannel nettyChannel = new NettyChannel(ctx.channel());
        channels.put(nettyChannel.id(), nettyChannel);
        System.out.println("连接【" + nettyChannel.id() + "】已建立，当前Map：" + channels.size() + " - 来源：" + ctx.channel().remoteAddress() + " JavaObject: " + ctx);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        // 连接断开删除掉Map中的内容
        DubheChannel channel = channels.remove(Channels.getChannelId(ctx));
        System.err.println("连接【" + Channels.getChannelId(ctx) + "】断开，当前Map：" + channels.size() + " - 来源：" + ctx.channel().remoteAddress() + " JavaObject: " + ctx);
        channel.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 读取内容
        byte[] bytes = ((byte[]) msg);
        DubheChannel channel = channels.get(Channels.getChannelId(ctx));

        System.out.println("接收到客户端发送过来的消息，大小：" + ((byte[]) msg).length);
        try {
            if (!checkUnpack(bytes)) {
                int i = 1;
                List<byte[]> unpacks = unpack(bytes);
                for (byte[] pack : unpacks) {
                    System.out.println("第" + i + "个包大小为：" + pack.length);
                    i++;

                    RequestHandler.handle(channel, serializer.decode(pack));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }

    @Override
    public void disconnect(DubheClient client) {
    }

}
