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

    private static final Serializer serializer = SerializerFactory.getSerializer()

    //
    // 如果 CACHE_SIZE = SIZE_BYTES_INCOMPLETE, 那么就代表当前内容连最基本的对象头都没读取完。
    // 在下一次读取的时候需要进行填充。
    //
    private static int CACHE_SIZE = 0

    private static int SIZE_BYTES_INCOMPLETE = -1

    //
    // 如果发生半包的情况下，那么多余的缓存内容放入该ByteBuf中
    //
    private static var BYTEBUF_CACHE = ByteBuf.allocate(ByteBuf.AUTO_CAPACITY)

    static void resetBYTEBUF_CACHE() {
        BYTEBUF_CACHE.clear()
        CACHE_SIZE = 0
    }

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

            // 处理半包
            if (!BYTEBUF_CACHE.isEmpty()) {
                // 对象SIZE内容是否完整
                if (CACHE_SIZE == SIZE_BYTES_INCOMPLETE) {
                    int filling = Bytes.INT_BYTE_SIZE - BYTEBUF_CACHE.size()
                    BYTEBUF_CACHE.put(Arrays.copyOfRange(array, 0, filling))
                    CACHE_SIZE = BYTEBUF_CACHE.asInt()
                    position += filling
                }

                if (CACHE_SIZE > length) {
                    BYTEBUF_CACHE.put(array)
                    CACHE_SIZE -= length
                    break
                } else {
                    // 进入到这里表示半包已经处理完成
                    BYTEBUF_CACHE.put(Arrays.copyOfRange(array, position, CACHE_SIZE))
                    position = CACHE_SIZE
                    objectBuffers.add(BYTEBUF_CACHE.copyOf(Bytes.INT_BYTE_SIZE))
                    resetBYTEBUF_CACHE()
                    continue
                }
            }

            if((length - position) < 4) {
                CACHE_SIZE = SIZE_BYTES_INCOMPLETE
                BYTEBUF_CACHE.put(Arrays.copyOfRange(array, position, length))
                break
            }

            int offset = Bytes.toInt(array, position) + Bytes.INT_BYTE_SIZE
            position += Bytes.INT_BYTE_SIZE

            if ((offset - Bytes.INT_BYTE_SIZE) > (length - position)) {
                BYTEBUF_CACHE.put(Arrays.copyOfRange(array, (position - Bytes.INT_BYTE_SIZE), length))
                if (BYTEBUF_CACHE.size() > 4) {
                    CACHE_SIZE = BYTEBUF_CACHE.asInt()
                    CACHE_SIZE -= BYTEBUF_CACHE.size() - Bytes.INT_BYTE_SIZE
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

    @Override
    void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        // 建立一个新的连接
        NettyChannel nettyChannel = new NettyChannel(ctx.channel())
        channels.put(nettyChannel.id(), nettyChannel)
    }

    @Override
    void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        // 连接断开删除掉Map中的内容
        var channel = channels.remove(Channels.getChannelId(ctx))
        println("连接【${Channels.getChannelId(ctx)}】断开，当前Map：${channels.size()} - 来源：${ctx.channel().remoteAddress()} JavaObject: $ctx")
    }

    @Override
    void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 读取内容
        byte[] bytes = ((byte[]) msg)
        DubheChannel channel = channels.get(Channels.getChannelId(ctx))

        try {
            int i = 1
            List<byte[]> unpacks = unpack(bytes)
            unpacks.each { pack ->
                i++

                RequestHandler.handle(channel, serializer.decode(pack))
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
