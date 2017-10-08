package net.ftzcode.client.handler

import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufUtil
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import net.ftzcode.protocol.Pandora
import org.slf4j.LoggerFactory

/**
 * Created by WangZiHe on 2017/10/6
 * QQ/WeChat:648830605
 * QQ-Group:368512253
 * Blog:www.520code.net
 * Github:https://github.com/yancheng199287
 */


class ProxyClientHandler(private var serverChannel: Channel, private var channelId: String) : SimpleChannelInboundHandler<ByteBuf>() {

    private val logger = LoggerFactory.getLogger("ProxyClientHandler")

    override fun channelRead0(ctx: ChannelHandlerContext?, msg: ByteBuf?) {
        val pandora = Pandora()
        pandora.msgType = Pandora.SEND_YC_SERVER_FROM_YC_CLIENT
        pandora.data = ByteBufUtil.getBytes(msg)
        pandora.channelId = channelId
        pandora.content = "成功获取到本地服务端的消息"
        serverChannel.writeAndFlush(pandora)

        logger.debug("获取到本地服务器响应的信息，发送给YCServer服务端：$pandora   ${String(pandora.data!!)}")
    }


    override fun channelInactive(ctx: ChannelHandlerContext?) {

        logger.debug("连接本地服务通道销毁了，通道信息：${ctx!!.channel()}")
    }
}