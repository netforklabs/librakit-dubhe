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

/* Create date: 2021/6/24 */

package com.netforklabs.rpc.server.netty

import com.netforklabs.rpc.api.DubheChannel
import com.netforklabs.framework.config.setting.NetforkSetting
import com.netforklabs.net.protocol.Commands
import com.netforklabs.net.protocol.message.ByteBufMessage
import com.netforklabs.net.protocol.message.Message

/**
 * @author luotsforever
 * @email luotsforevers@foxmail.com
 */
@SuppressWarnings("JavaDoc")
class RequestHandler {

    /**
     * 处理消息
     *
     * @param message 消息对象
     */
    static void handle(DubheChannel channel, Message message)
    {

        // 检测魔数是否满足协议要求
        if (message.getMagicNumber() != NetforkSetting.MAGIC) {
            return
        }

        switch (message.cmd()) {
            case Commands.HELLO:
                println "Message Hello"
                break

            case Commands.CONNECT:
                println "Message Connect"
                break

            case Commands.INVOKE:
                println(((ByteBufMessage) message).asString())
                break

            case Commands.APPLY_FOR_REG:
                println "Message apply for reg"
                break

            case Commands.DISCONNECT:
                println "Message disconnect"
                break

            case Commands.HEART_BEAT:
                println "Message heart beat"
                break

            case Commands.SIZE:
                println "Message SIZE"
                break
        }
    }

}
