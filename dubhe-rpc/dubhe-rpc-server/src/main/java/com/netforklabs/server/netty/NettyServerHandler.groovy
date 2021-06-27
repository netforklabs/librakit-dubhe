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

package com.netforklabs.server.netty

import com.netforklabs.api.DubheChannel
import com.netforklabs.api.DubheClient
import com.netforklabs.api.DubheServerHandler
import com.netforklabs.netprotocol.serializer.Serializer
import com.netforklabs.netprotocol.serializer.SerializerFactory
import com.netforklabs.server.Channels
import com.netforklabs.utils.bytes.ByteBuf
import com.netforklabs.utils.bytes.Bytes
import io.netty.channel.ChannelHandlerAdapter
import io.netty.channel.ChannelHandlerContext

/**
 * @author orval* @email orvals@foxmail.com
 */
@SuppressWarnings("JavaDoc")
class NettyServerHandler extends ChannelHandlerAdapter
        implements DubheServerHandler {

    private final Map<String, DubheChannel> channels = new HashMap<>()

    private static int cacheSize    = 0

    //
    // 如果发生半包的情况下，那么多余的缓存内容放入该ByteBuf中
    //
    private static final Serializer serializer = SerializerFactory.getSerializer()

    private static var byteBufCache = ByteBuf.allocate(ByteBuf.AUTO_CAPACITY)

    /**
     * 拆包
     *
     * @param array 字节数组
     */
    static List<byte[]> unpack(byte[] array) {
        List<byte[]> objectBuffers = new ArrayList<>()

        var position = 0
        var length = array.length
        while (position < length) {

            if(!byteBufCache.isEmpty()) {
                if(cacheSize > length) {
                    byteBufCache.put(array)
                    cacheSize -= length
                    break
                } else {
                    // 进入到这里表示半包已经处理完成
                    byteBufCache.put(Arrays.copyOfRange(array, 0, cacheSize))
                    position = cacheSize
                    objectBuffers.add(byteBufCache.copyOf(4))
                    byteBufCache.clear()
                    continue
                }
            }

            int offset = Bytes.toInt(array, position) + Bytes.INT_BYTE_SIZE
            position += 4

            if(offset > (length - position) ) {
                byteBufCache.put(Arrays.copyOfRange(array, (position - Bytes.INT_BYTE_SIZE), length))
                if(byteBufCache.size() > 4) {
                    cacheSize = byteBufCache.asInt()
                    cacheSize -= byteBufCache.size() - Bytes.INT_BYTE_SIZE
                }
                break
            }

            // 这是一个完整的字节数组对象
            var objLength = offset - Bytes.INT_BYTE_SIZE
            var object = new byte[objLength]

            System.arraycopy(array, position, object, 0, objLength)
            objectBuffers.add(object)

            position += object.length
        }

        return objectBuffers
    }

    /**
     * 处理半包问题
     */
    static void halfpack(byte[] array, int position, int length) {
        var size = Bytes.toInt(byteBufCache.copyOf(0, 3))
    }

    /**
     * 是否需要进行拆包，如果前四个字节的整型数组等于array的大小，就代表不需要
     * 拆包当前只有一个对象上传。如果大于就代表需要进行拆包操作。
     *
     * @param array 字节数组
     * @return true | false
     */
    private static boolean checkUnpack(byte[] array) {
        return Bytes.toInt(array, 0) != (array.length - 4)
    }

    @Override
    void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        // 建立一个新的连接
        NettyChannel nettyChannel = new NettyChannel(ctx.channel())
        channels.put(nettyChannel.id(), nettyChannel)
        println("连接【${Channels.getChannelId(ctx)}】建立，当前Map：${channels.size()} - 来源：${ctx.channel().remoteAddress()} JavaObject: $ctx")
    }

    @Override
    void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        // 连接断开删除掉Map中的内容
        var channel = channels.remove(Channels.getChannelId(ctx))
        println("连接【${Channels.getChannelId(ctx)}】断开，当前Map：${channels.size()} - 来源：${ctx.channel().remoteAddress()} JavaObject: $ctx")
        channel.close()
    }

    @Override
    void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 读取内容
        byte[] bytes = ((byte[]) msg)
        DubheChannel channel = channels.get(Channels.getChannelId(ctx))

        println("接收到客户端发送过来的消息，大小：${((byte[]) msg).length}")
        try {
            if (checkUnpack(bytes)) {
                int i = 1
                List<byte[]> unpacks = unpack(bytes)
                unpacks.each { pack ->
                    println("第${i}个包大小为：$pack.length")
                    i++

                    RequestHandler.handle(channel, serializer.decode(pack))
                }
            }

        } catch (Exception e) {
            e.printStackTrace()
        }
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

}
