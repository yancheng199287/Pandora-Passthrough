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

    // private val logger = LoggerFactory.getLogger("YCMessageEncoder")


    override fun encode(ctx: ChannelHandlerContext?, pandora: Pandora?, out: ByteBuf?) {
        var frameLength: Int = Constant.HEADER_LENGTH
        frameLength += pandora!!.data!!.size
        if (pandora.content == null) {
            out!!.writeInt(frameLength)
            out.writeInt(0)
        } else {
            //注意，字符串应该这样获取length，当然如果英文数字可以直接获取长度
            val contentLength = pandora.content!!.toByteArray().size
            frameLength += contentLength
            out!!.writeInt(frameLength)
            out.writeInt(contentLength)
            out.writeCharSequence(pandora.content, Charset.defaultCharset())
        }
        out.writeByte(pandora.msgType!!.toInt())

        out.writeCharSequence(pandora.channelId, Charset.defaultCharset())

        out.writeBytes(pandora.data)

    }

}