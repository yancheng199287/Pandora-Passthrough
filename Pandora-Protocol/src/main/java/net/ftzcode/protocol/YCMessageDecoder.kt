package net.ftzcode.protocol

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import org.slf4j.LoggerFactory
import java.nio.charset.Charset

/**
 * Created by WangZiHe on 2017/10/6
 * QQ/WeChat:648830605
 * QQ-Group:368512253
 * Blog:www.520code.net
 * Github:https://github.com/yancheng199287
 */

class YCMessageDecoder : ByteToMessageDecoder() {

   // private val logger = LoggerFactory.getLogger("YCMessageDecoder")


    override fun decode(ctx: ChannelHandlerContext?, byteBuf: ByteBuf?, out: MutableList<Any>?) {
        if (byteBuf == null) {
            return
        }
        if (byteBuf.readableBytes() < Constant.HEADER_LENGTH) {
            return
        }
        val frameLength = byteBuf.readInt()

        if (frameLength < Constant.HEADER_LENGTH) {
            return
        }

        val readableLength = byteBuf.readableBytes()

        if (readableLength < frameLength - Constant.LENGTH_FILED) {
            return
        }

        val pandora = Pandora()

        val contentLength = byteBuf.readInt()
        if (contentLength > 0) {
            pandora.content = byteBuf.readCharSequence(contentLength, Charset.defaultCharset()).toString()
        }

        pandora.msgType = byteBuf.readByte()

        pandora.channelId = byteBuf.readCharSequence(Constant.CHANNEL_ID_LENGTH, Charset.defaultCharset()).toString()

        val data = ByteArray(frameLength - Constant.HEADER_LENGTH - contentLength)
        byteBuf.readBytes(data)
        pandora.data = data
        out!!.add(pandora)
    }

}