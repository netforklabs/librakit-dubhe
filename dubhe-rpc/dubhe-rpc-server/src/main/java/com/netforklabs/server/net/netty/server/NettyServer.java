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

/* Create date: 2021/6/20 */

package com.netforklabs.server.net.netty.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * RPC服务端
 *
 * @author orval
 * @email orvlas@foxmail.com
 */
@SuppressWarnings("JavaDoc")
public class NettyServer {

    private static final List<SocketChannel> socketChannels = new ArrayList<>();

    // 创建 Selector
    private static Selector selector;

    /**
     * 创建服务端
     *
     * @param port 端口
     */
    public static void createServer(int port) throws IOException {

        if(selector == null)
             selector = Selector.open();

        ServerSocketChannel serverChannel = ServerSocketChannel.open();

        // 将服务模式切换成非阻塞模式
        serverChannel.configureBlocking(false);

        // 绑定监听端口
        serverChannel.bind(new InetSocketAddress(port));

        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true)
        {
            // 如果没有Accept事件发生，selector就阻塞
            selector.select();

            // 处理事件
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectionKeys.iterator();
            while(keyIterator.hasNext())
            {
                SelectionKey key = keyIterator.next();

                // 处理新的客户端链接
                switch (key.readyOps())
                {
                    case SelectionKey.OP_ACCEPT:
                        SocketChannel socketChannel = serverChannel.accept();
                        configurationChannel(socketChannel, SelectionKey.OP_READ);

                        System.out.println(socketChannel.getLocalAddress().toString());
                        break;

                    case SelectionKey.OP_READ:
                        break;
                }

                selectionKeys.remove(key);
            }

        }

    }

    /**
     * 配置 SocketChannel
     *
     * @param socketChannel SocketChannel 对象
     * @param selectorOp    多路复用器操作
     * @throws IOException
     */
    private static void configurationChannel(SocketChannel socketChannel, int selectorOp) throws IOException {
        // 切换成非阻塞模式
        socketChannel.configureBlocking(false);
        socketChannels.add(socketChannel);

        // 注册多路复用
        socketChannel.register(selector, selectorOp);
    }

}
