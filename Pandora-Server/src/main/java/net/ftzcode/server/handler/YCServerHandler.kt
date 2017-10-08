package net.ftzcode.server.handler

import io.netty.channel.*
import net.ftzcode.protocol.Pandora
import net.ftzcode.server.util.AppConf
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

/**
 * Created by WangZiHe on 2017/10/6
 * QQ/WeChat:648830605
 * QQ-Group:368512253
 * Blog:www.520code.net
 * Github:https://github.com/yancheng199287
 */

class YCServerHandler : SimpleChannelInboundHandler<Pandora>() {

    private val logger = LoggerFactory.getLogger("YCServerHandler")

    companion object {
        var channelMap = ConcurrentHashMap<String, Channel>(10)
        var yClientChannel: Channel? = null
    }

    override fun channelActive(ctx: ChannelHandlerContext?) {
        logger.debug("来自连接YCServer的通道激活   ${ctx!!.channel()}  ,通道id：${ctx!!.channel().id().asLongText()}")
    }

    override fun channelRead0(ctx: ChannelHandlerContext?, pandora: Pandora?) {

        logger.debug("YCServer 接收到消息， 当前 通道信息:${ctx!!.channel()}  获取的消息内容: $pandora")
        when (pandora!!.msgType) {

            Pandora.SEND_YC_CLIENT_FROM_YC_SERVER -> {
                sendMsgToYClient(ctx, pandora)

                logger.debug("YCServer接收到消息... 是服务端请求发送的")

            }

            Pandora.SEND_YC_SERVER_FROM_YC_CLIENT -> {
                logger.debug("YCServer接收到消息... 是客户端请求响应发送的")

                sendMsgToProxyClient(pandora)
            }


            Pandora.SEND_YC_SERVER_NO_RESPONSE_FROM_YC_CLIENT -> {
                logger.info("请求客户端失败，返回的原因:${pandora.content}")
                yClientChannel!!.close()
                yClientChannel = null
            }

            Pandora.YC_CLIENT_REQUEST_LOGIN -> {
                if (AppConf.accessKey == pandora.content) {
                    logger.info("恭喜，认证成功！")
                    pandora.content = "恭喜你，认证成功!"
                    pandora.msgType = Pandora.YC_CLIENT_RESPONSE_LOGIN_SUCCESS
                    yClientChannel = ctx.channel()
                    yClientChannel!!.writeAndFlush(pandora)
                } else {
                    logger.info("认证失败 ！")
                    pandora.content = "很抱歉，认证失败!"
                    pandora.msgType = Pandora.YC_CLIENT_RESPONSE_LOGIN_FAILED
                    yClientChannel = null
                    ctx.writeAndFlush(pandora).addListener(ChannelFutureListener.CLOSE)
                }

            }

            else -> {
                logger.info("接收到不可识别的消息")
            }
        }


    }


    private fun sendMsgToProxyClient(pandora: Pandora) {
        val proxyClientChannel = channelMap[pandora.channelId]
        proxyClientChannel!!.writeAndFlush(pandora)
    }


    private fun sendMsgToYClient(ctx: ChannelHandlerContext, pandora: Pandora) {
        val channel = ctx.channel()
        val channelId = channel.id().asLongText()
        channelMap.put(channelId, channel)
        pandora.channelId = channelId
        yClientChannel!!.writeAndFlush(pandora)
    }

    override fun channelInactive(ctx: ChannelHandlerContext?) {
        val channel = ctx!!.channel()
        channelMap.remove(channel.id().asLongText())

        logger.debug("连接YCServer的客户端通道销毁，当前已经被移除缓存：$channel")

    }


}