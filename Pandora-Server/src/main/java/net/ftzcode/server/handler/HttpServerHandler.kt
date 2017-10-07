package net.ftzcode.server.handler

import io.netty.bootstrap.Bootstrap
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufUtil
import io.netty.buffer.Unpooled
import io.netty.channel.*
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.LengthFieldBasedFrameDecoder
import io.netty.handler.codec.LengthFieldPrepender
import io.netty.handler.timeout.IdleStateHandler
import net.ftzcode.protocol.LengthFiledDecoder
import net.ftzcode.protocol.Pandora
import net.ftzcode.protocol.YCMessageDecoder
import net.ftzcode.protocol.YCMessageEncoder
import net.ftzcode.server.util.AppConf
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

/**
 * Created by WangZiHe on 2017/10/6
 * QQ/WeChat:648830605
 * QQ-Group:368512253
 * Blog:www.520code.net
 * Github:https://github.com/yancheng199287
 */


class HttpServerHandler : ChannelInboundHandlerAdapter() {

   // private val logger = LoggerFactory.getLogger("HttpServerHandler")

    private var completeByteBuf: ByteBuf = Unpooled.directBuffer()


    override fun channelActive(ctx: ChannelHandlerContext?) {
       // logger.info("来自连接HttpServerHandler  的通道激活   $ctx!!.channel  ,通道id：${ctx!!.channel().id().asLongText()}")
        completeByteBuf.clear()
    }

    override fun channelRead(ctx: ChannelHandlerContext?, msg: Any?) {
        val byteBuf = msg as ByteBuf

        // logger.info("HttpServerHandler 可读容量：${byteBuf.readableBytes()} 接收到的浏览器数据的数据是： $this,  $ctx!!.channel,, ,请求的内容是：$byteBuf")

        completeByteBuf.writeBytes(byteBuf.copy())
    }

    override fun channelReadComplete(ctx: ChannelHandlerContext?) {
        //  logger.info("http消息读取完成，  可读 容量: ${completeByteBuf.readableBytes()}  $completeByteBuf，   ，$this， ${ctx!!.channel()}")

        forwardRequestToYCServer(ctx!!, completeByteBuf)

    }


    private fun forwardRequestToYCServer(ctx: ChannelHandlerContext, msg: ByteBuf) {
        val httpInboundChannel = ctx.channel()
        val b = Bootstrap()
        b.group(httpInboundChannel.eventLoop())
                .channel(NioSocketChannel::class.java)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(object : ChannelInitializer<SocketChannel>() {
                    override fun initChannel(ch: SocketChannel?) {
                        ch!!.pipeline()
                                .addLast(IdleStateHandler(0, 0, 30, TimeUnit.SECONDS))
                                .addLast(LengthFieldBasedFrameDecoder(LengthFiledDecoder.MAX_FRAME_LENGTH, LengthFiledDecoder.LENGTH_FIELD_OFFSET,
                                        LengthFiledDecoder.LENGTH_FIELD_LENGTH, LengthFiledDecoder.LENGTH_ADJUSTMENT, LengthFiledDecoder.INITIAL_BYTES_TO_STRIP, true))
                                .addLast(YCMessageDecoder())
                                .addLast(LengthFieldPrepender(4))
                                .addLast(YCMessageEncoder())
                                .addLast(ClientProxyHandler(httpInboundChannel))

                    }
                })

        val f = b.connect(AppConf.localHost, AppConf.serverPort)

        f.addListener({ future ->
            if (future.isSuccess) {
                val channel = f.channel()
                val pandora = Pandora()
                pandora.msgType = Pandora.SEND_YC_CLIENT_FROM_YC_SERVER
                pandora.channelId = channel.id().asLongText()
                pandora.data = ByteBufUtil.getBytes(msg)
                channel.writeAndFlush(pandora).addListener {
                    completeByteBuf.clear()
                }
            }
        })
    }


    override fun channelInactive(ctx: ChannelHandlerContext?) {
        completeByteBuf.clear()
    }

}