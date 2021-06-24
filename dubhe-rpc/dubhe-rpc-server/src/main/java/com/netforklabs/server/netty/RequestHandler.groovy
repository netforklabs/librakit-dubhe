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

package com.netforklabs.server.netty

import com.netforklabs.api.DubheChannel
import com.netforklabs.netprotocol.Commands
import com.netforklabs.netprotocol.Message

/**
 * @author orval
 * @email orvals@foxmail.com
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
        switch (message.cmd()) {
            case Commands.HELLO:
                println "Message Hello"
                break

            case Commands.CONNECT:
                println "Message Connect"
                break

            case Commands.CALL:
                println "Message call"
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

            case Commands.SIZEOF:
                println "Message sizeof"
                break
        }
    }

}
