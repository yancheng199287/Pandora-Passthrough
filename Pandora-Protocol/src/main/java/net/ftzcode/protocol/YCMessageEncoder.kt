package net.ftzcode.protocol

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder
import org.slf4j.LoggerFactory
import java.nio.charset.Charset

/**
 * Created by WangZiHe on 2017/10/6
 * QQ/WeChat:648830605
 * QQ-Group:368512253
 * Blog:www.520code.net
 * Github:https://github.com/yancheng199287
 */

class YCMessageEncoder : MessageToByteEncoder<Pandora>() {

    private val logger = LoggerFactory.getLogger("YCMessageEncoder")

    override fun encode(ctx: ChannelHandlerContext?, pandora: Pandora?, out: ByteBuf?) {
        logger.debug("开始编码...  当前 通道：${ctx!!.channel()}, 要编码的对象pandora：$pandora")

        var frameLength: Int = Constant.HEADER_LENGTH
        frameLength += pandora!!.data!!.size
        if (pandora.content == null) {
            out!!.writeInt(frameLength)
            out.writeInt(0)
        } else {
            //注意，字符串应该按照指定编码获取字节数组的长度length，当然如果英文数字可以直接获取字符长度，推荐前者
            val contentLength = pandora.content!!.toByteArray(Constant.CHARSET).size
            frameLength += contentLength
            out!!.writeInt(frameLength)
            out.writeInt(contentLength)
            out.writeCharSequence(pandora.content, Constant.CHARSET)
        }

        logger.debug("编码中，设置标头的总长度是 $frameLength，承载的协议数据dataSize：${pandora.data!!.size}..")

        out.writeByte(pandora.msgType!!.toInt())

        val size = out.writeCharSequence(pandora.channelId, Constant.CHARSET)

        logger.debug("编码中， 计算 通道ID  size的大小:$size")

        out.writeBytes(pandora.data)

        logger.debug("编码成功... 要传输的容量信息：$out")
    }

}