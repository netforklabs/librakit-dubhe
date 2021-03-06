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
import com.netforklabs.rpc.api.DubheServerHandler
import com.netforklabs.net.protocol.serializer.Serializer
import com.netforklabs.net.protocol.serializer.SerializerFactory
import com.netforklabs.rpc.server.Channels
import com.netforklabs.framework.utils.bytes.ByteBuf
import com.netforklabs.framework.utils.bytes.Bytes
import io.netty.channel.ChannelHandlerAdapter
import io.netty.channel.ChannelHandlerContext

/**
 * @author luotsforever* @email luotsforevers@foxmail.com
 */
@SuppressWarnings("JavaDoc")
class NettyServerHandler extends ChannelHandlerAdapter
        implements DubheServerHandler {

    private final Map<String, DubheChannel> channels = new HashMap<>()

    private static final Serializer serializer = SerializerFactory.getSerializer()

    //
    // ?????? CACHE_SIZE = SIZE_BYTES_INCOMPLETE, ?????????????????????????????????????????????????????????????????????
    // ????????????????????????????????????????????????
    //
    private static int CACHE_SIZE = 0

    private static int SIZE_BYTES_INCOMPLETE = -1

    //
    // ?????????????????????????????????????????????????????????????????????ByteBuf???
    //
    private static var BYTEBUF_CACHE = ByteBuf.allocate(ByteBuf.AUTO_CAPACITY)

    private static var CLIENT_FORCE_CLOSE = "An existing connection was forcibly closed by the remote host"

    static void resetBYTEBUF_CACHE() {
        BYTEBUF_CACHE.clear()
        CACHE_SIZE = 0
    }

    /**
     * ??????
     *
     * @param array ????????????
     */
    static List<byte[]> unpack(byte[] array) {
        List<byte[]> objectBuffers = new ArrayList<>()

        var position = 0
        var length = array.length
        while (position < length) {

            // ????????????
            if (!BYTEBUF_CACHE.isEmpty()) {
                // ??????SIZE??????????????????
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
                    // ?????????????????????????????????????????????
                    BYTEBUF_CACHE.put(Arrays.copyOfRange(array, position, CACHE_SIZE))
                    position += CACHE_SIZE
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

            // ???????????????????????????????????????
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
        // ????????????????????????
        NettyChannel nettyChannel = new NettyChannel(ctx.channel())
        channels.put(nettyChannel.id(), nettyChannel)
    }

    @Override
    void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        // ?????????????????????Map????????????
        channels.remove(Channels.getChannelId(ctx))
        // FIXME: ?????????????????????
        println("?????????${Channels.getChannelId(ctx)}??????????????????Map???${channels.size()} - ?????????${ctx.channel().remoteAddress()} JavaObject: $ctx")
        ctx.close()
    }

    @Override
    void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // ????????????
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
        if(cause.message == CLIENT_FORCE_CLOSE) {
            // FIXME: ?????????????????????
            println("ERROR - $cause.message")
        }
    }

    @Override
    void disconnect(DubheClient client) {
    }

}
