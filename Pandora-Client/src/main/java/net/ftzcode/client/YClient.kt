package net.ftzcode.client

import io.netty.bootstrap.Bootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.LengthFieldBasedFrameDecoder
import io.netty.handler.codec.LengthFieldPrepender
import net.ftzcode.client.handler.YClientHandler
import net.ftzcode.protocol.LengthFiledDecoder
import net.ftzcode.protocol.YCMessageDecoder
import net.ftzcode.protocol.YCMessageEncoder
import net.ftzcode.client.util.AppConf
import org.slf4j.LoggerFactory

/**
 * Created by WangZiHe on 2017/10/6
 * QQ/WeChat:648830605
 * QQ-Group:368512253
 * Blog:www.520code.net
 * Github:https://github.com/yancheng199287
 */

class YClient{

  //  private val logger = LoggerFactory.getLogger("YClient")
    fun startYClient() {
        val group = NioEventLoopGroup()
        val b = Bootstrap()
        try {
            b.group(group)
                    .channel(NioSocketChannel::class.java)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .remoteAddress(AppConf.remoteHost, AppConf.remotePort)
                    .handler(object : ChannelInitializer<SocketChannel>() {
                        override fun initChannel(ch: SocketChannel?) {
                            ch!!.pipeline()
                                    .addLast(LengthFieldBasedFrameDecoder(LengthFiledDecoder.MAX_FRAME_LENGTH,
                                            LengthFiledDecoder.LENGTH_FIELD_OFFSET,LengthFiledDecoder.LENGTH_FIELD_LENGTH,LengthFiledDecoder.LENGTH_ADJUSTMENT,LengthFiledDecoder.INITIAL_BYTES_TO_STRIP,true))
                                    .addLast(YCMessageDecoder())
                                    .addLast(LengthFieldPrepender(4))
                                    .addLast(YCMessageEncoder())
                                    .addLast(YClientHandler())
                        }
                    })
            val f = b.connect().sync()
            f.channel().closeFuture().sync()
        } finally {
            group.shutdownGracefully()
        }

    }


}