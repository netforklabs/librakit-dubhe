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

package com.netforklabs.rpc.test;

import com.netforklabs.rpc.api.DubheChannel;
import com.netforklabs.rpc.api.DubheClient;
import com.netforklabs.framework.config.setting.NetforkSetting;
import com.netforklabs.framework.config.setting.Server;
import com.netforklabs.net.protocol.Commands;
import com.netforklabs.net.protocol.message.ByteBufMessage;
import com.netforklabs.net.protocol.message.HelloMessage;
import com.netforklabs.rpc.server.netty.NettyClient;
import com.netforklabs.framework.utils.bytes.ByteBuf;

import java.nio.charset.StandardCharsets;

/**
 * @author orval
 * @email orvlas@foxmail.com
 */
@SuppressWarnings("JavaDoc")
public class StartClient {

    public static void main1(String... args) throws InterruptedException {
        NetforkSetting setting = NetforkSetting.compile();

        DubheClient client = new NettyClient();

        for (Server server : setting.getServers()) {
            DubheChannel channel = client.connect(server.getHost(), server.getPort());
            channel.send(new HelloMessage());

            ByteBufMessage byteBuffer = new ByteBufMessage() {
                @Override
                public int cmd() {
                    return Commands.INVOKE;
                }
            };

            StringBuilder bigString = new StringBuilder();
            for(int i=0; i < 1024; i++) {
                bigString.append(i);
            }

            byte[] bytes1 = bigString.toString().getBytes(StandardCharsets.UTF_8);
            byteBuffer.setByteBuf(ByteBuf.allocate(bytes1.length, bytes1));

            channel.send(byteBuffer);
        }

        while(true) {
            Thread.sleep(1000);
        }

    }

    public static void main(String... args) throws InterruptedException {
        NetforkSetting setting = NetforkSetting.compile();

        DubheClient client = new NettyClient();

        for (Server server : setting.getServers()) {
            DubheChannel channel = client.connect(server.getHost(), server.getPort());
            ByteBufMessage byteBuffer = new ByteBufMessage() {
                @Override
                public int cmd() {
                    return Commands.INVOKE;
                }
            };

            ByteBuf buf = ByteBuf.allocate(1024 - 114);
            for(int i=0; i < 1024 - 114; i++) {
                buf.put((byte) i);
            }

            byteBuffer.setByteBuf(buf);

            channel.send(byteBuffer);
            channel.send(new HelloMessage());
        }

        while(true) {
            Thread.sleep(1000);
        }

    }

}
