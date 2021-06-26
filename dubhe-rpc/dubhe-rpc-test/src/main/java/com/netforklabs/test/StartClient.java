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

package com.netforklabs.test;

import com.netforklabs.api.DubheChannel;
import com.netforklabs.api.DubheClient;
import com.netforklabs.config.setting.NetforkSetting;
import com.netforklabs.config.setting.Server;
import com.netforklabs.netprotocol.Commands;
import com.netforklabs.netprotocol.message.ByteBuf_M1;
import com.netforklabs.netprotocol.message.Hello_M;
import com.netforklabs.server.netty.NettyClient;
import com.netforklabs.utils.bytes.ByteBuf;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * @author orval
 * @email orvlas@foxmail.com
 */
@SuppressWarnings("JavaDoc")
public class StartClient {

    public static void main(String[] args) throws InterruptedException {
        NetforkSetting setting = NetforkSetting.compile();

        DubheClient client = new NettyClient();

        for (Server server : setting.getServers()) {
            DubheChannel channel = client.connect(server.getHost(), server.getPort());
            channel.send(new Hello_M());
            channel.send(new Hello_M());

            ByteBuf_M1 byteBuffer = new ByteBuf_M1() {
                @Override
                public int cmd() {
                    return Commands.CALL;
                }
            };

            byte[] bytes = "服务端你好，我是客户端".getBytes(StandardCharsets.UTF_8);
            byteBuffer.setBuf(ByteBuf.allocate(bytes.length, bytes));

            ByteBuf_M1 byteBuffer1 = new ByteBuf_M1() {
                @Override
                public int cmd() {
                    return Commands.CALL;
                }
            };

            byte[] bytes1 = "1234567890".getBytes(StandardCharsets.UTF_8);
            byteBuffer1.setBuf(ByteBuf.allocate(bytes1.length, bytes1));

            channel.send(byteBuffer);
            channel.send(byteBuffer1);
        }

        TimeUnit.SECONDS.sleep(20);

    }

}
