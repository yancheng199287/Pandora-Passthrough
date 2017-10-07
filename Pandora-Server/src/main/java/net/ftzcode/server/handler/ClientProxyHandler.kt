package net.ftzcode.server.handler

import io.netty.buffer.Unpooled
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.timeout.IdleState
import io.netty.handler.timeout.IdleStateEvent
import net.ftzcode.protocol.Pandora

/**
 * Created by WangZiHe on 2017/10/6
 * QQ/WeChat:648830605
 * QQ-Group:368512253
 * Blog:www.520code.net
 * Github:https://github.com/yancheng199287
 */

class ClientProxyHandler(private var httpChannel: Channel) : SimpleChannelInboundHandler<Pandora>() {

    override fun channelRead0(ctx: ChannelHandlerContext?, pandora: Pandora?) {
        httpChannel.writeAndFlush(Unpooled.copiedBuffer(pandora!!.data))
    }

    override fun channelInactive(ctx: ChannelHandlerContext?) {
        httpChannel.close()
    }

    //保证短连接的运行机制
    override fun userEventTriggered(ctx: ChannelHandlerContext?, evt: Any?) {
        if (evt is IdleStateEvent) {
            var type = ""
            when {
                evt.state() == IdleState.READER_IDLE -> type = "read idle"
                evt.state() == IdleState.WRITER_IDLE -> type = "write idle"
                evt.state() == IdleState.ALL_IDLE -> {
                    type = "all idle"
                    ctx!!.close()
                }
            }
        }
    }
}
