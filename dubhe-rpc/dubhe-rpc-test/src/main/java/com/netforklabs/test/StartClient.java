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
import com.netforklabs.netprotocol.message.ByteBufMessage;
import com.netforklabs.netprotocol.message.HelloMessage;
import com.netforklabs.server.netty.NettyClient;
import com.netforklabs.utils.bytes.ByteBuf;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

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
                    return Commands.CALL;
                }
            };

            StringBuilder bigString = new StringBuilder();
            for(int i=0; i < 1024; i++) {
                bigString.append(i);
            }

            byte[] bytes1 = bigString.toString().getBytes(StandardCharsets.UTF_8);
            byteBuffer.setBuf(ByteBuf.allocate(bytes1.length, bytes1));

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
                    return Commands.CALL;
                }
            };

            ByteBuf buf = ByteBuf.allocate(1024 - 114);
            for(int i=0; i < 1024 - 114; i++) {
                buf.put((byte) i);
            }

            byteBuffer.setBuf(buf);

            channel.send(byteBuffer);
            channel.send(new HelloMessage());
        }

        while(true) {
            Thread.sleep(1000);
        }

    }

}
