package net.ftzcode.client.handler

import io.netty.bootstrap.Bootstrap
import io.netty.buffer.ByteBufUtil
import io.netty.buffer.Unpooled
import io.netty.channel.*
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import net.ftzcode.protocol.Pandora
import net.ftzcode.client.util.AppConf
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

/**
 * Created by WangZiHe on 2017/10/6
 * QQ/WeChat:648830605
 * QQ-Group:368512253
 * Blog:www.520code.net
 * Github:https://github.com/yancheng199287
 */


class YClientHandler : SimpleChannelInboundHandler<Pandora>() {

    private val logger = LoggerFactory.getLogger("YClientHandler")

    override fun channelActive(ctx: ChannelHandlerContext?) {
        val pandora = Pandora()
        pandora.msgType = Pandora.YC_CLIENT_REQUEST_LOGIN
        pandora.content = AppConf.accessKey
        pandora.data = Unpooled.EMPTY_BUFFER.array()
        pandora.channelId = ctx!!.channel().id().asLongText()
        ctx.writeAndFlush(pandora)
        logger.info("连接服务端的通道激活,开始进行认证登录...")
    }


    override fun channelRead0(ctx: ChannelHandlerContext?, pandora: Pandora?) {

        when (pandora!!.msgType) {
            Pandora.SEND_YC_CLIENT_FROM_YC_SERVER -> {
                startProxyClient(ctx!!.channel(), pandora)
            }

            Pandora.YC_CLIENT_RESPONSE_LOGIN_SUCCESS -> {
                logger.info("登录成功,服务端返回:${pandora.content} ")
            }
            Pandora.YC_CLIENT_RESPONSE_LOGIN_FAILED -> {
                logger.info("登录失败,服务端返回:${pandora.content}")
            }
            else -> {
                logger.info("客户端接收到不可识别的消息")
            }
        }


    }

    private fun startProxyClient(serverChannel: Channel, pandora: Pandora) {
        val b = Bootstrap()
        b.group(serverChannel.eventLoop())
                .channel(NioSocketChannel::class.java)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(object : ChannelInitializer<SocketChannel>() {
                    override fun initChannel(ch: SocketChannel?) {
                        ch!!.pipeline()
                                .addLast(ProxyClientHandler(serverChannel,pandora.channelId!!))
                    }
                })
        val f = b.connect(AppConf.localHost, AppConf.localServerPort)

        f.addListener({ future ->
            if (future.isSuccess) {
                val channel = f.channel()
                channel.writeAndFlush(Unpooled.copiedBuffer(pandora.data))

              // 浏览器多次刷新 一个请求 会 分两次包发送  导致 多个客户端端口 请求 ，  必须保证在同 一个端口才能完整请求 响应
                //解决办法，在源头聚合完整的请求消息
               // println("写入  $channel  服务器服务器信息：$pandora  \n  ${String(pandora.data!!)}")
            }else{
                logger.warn("Pandora-Client 请求本地服务无响应，请检查对应端口 ${AppConf.localServerPort} 的服务是否成功开启，服务端即将断开本次连接！")
                pandora.msgType = Pandora.SEND_YC_SERVER_NO_RESPONSE_FROM_YC_CLIENT
                pandora.data =Unpooled.EMPTY_BUFFER.array()
                pandora.content = "客户端请求本地服务无响应，申请断开连接！"
                serverChannel.writeAndFlush(pandora)
            }
        })
    }
}