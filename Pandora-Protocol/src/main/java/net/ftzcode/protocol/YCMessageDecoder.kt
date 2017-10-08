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

    private val logger = LoggerFactory.getLogger("YCMessageDecoder")


    override fun decode(ctx: ChannelHandlerContext?, byteBuf: ByteBuf?, out: MutableList<Any>?) {

        logger.debug("开始解码中， 当前进入的通道: ${ctx!!.channel()}, 取到的ByteBuf: $byteBuf")

        if (byteBuf == null) {
            return
        }

        logger.debug("解码中取到的byteBuf不为空，获取可读的字节容量:${byteBuf.readableBytes()}")

        if (byteBuf.readableBytes() < Constant.HEADER_LENGTH) {
            return
        }
        val frameLength = byteBuf.readInt()

        logger.debug("解码中正在读取总长度frameLength：$frameLength, 当前必须保证的header长度是:${Constant.HEADER_LENGTH}, 读完总长度frameLength，还剩下:${byteBuf.readableBytes()}")

        if (frameLength < Constant.HEADER_LENGTH) {
            return
        }

        val readableLength = byteBuf.readableBytes()

        logger.debug("读取完总长度剩下可读容量：$readableLength ，    必须要求大于或等于当前剩下的可读长度：${frameLength - Constant.LENGTH_FILED}")

        if (readableLength < frameLength - Constant.LENGTH_FILED) {
            return
        }
        val pandora = Pandora()

        val contentLength = byteBuf.readInt()
        if (contentLength > 0) {
            pandora.content = byteBuf.readCharSequence(contentLength,  Constant.CHARSET).toString()
        }

        pandora.msgType = byteBuf.readByte()

        pandora.channelId = byteBuf.readCharSequence(Constant.CHANNEL_ID_LENGTH,  Constant.CHARSET).toString()

        val data = ByteArray(frameLength - Constant.HEADER_LENGTH - contentLength)
        byteBuf.readBytes(data)
        pandora.data = data
        out!!.add(pandora)

        logger.debug("解码完成， 当前获取的对象pandora： $pandora")
    }

}