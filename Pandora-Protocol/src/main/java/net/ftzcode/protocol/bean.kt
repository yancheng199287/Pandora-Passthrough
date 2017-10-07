package net.ftzcode.protocol

import java.io.Serializable
import java.util.*

/**
 * Created by WangZiHe on 2017/10/6
 * QQ/WeChat:648830605
 * QQ-Group:368512253
 * Blog:www.520code.net
 * Github:https://github.com/yancheng199287
 */

data class Pandora(
        var msgType: Byte? = null,
        var channelId: String? = null,
        var content: String? = null,
        var data: ByteArray? = null
): Serializable {

    companion object {

        //从ycServer发送消息到yc客户端
        val YC_CLIENT_REQUEST_LOGIN: Byte = 0x10

        //响应登录失败
        val YC_CLIENT_RESPONSE_LOGIN_FAILED: Byte = 0x11

        //响应登录成功
        val YC_CLIENT_RESPONSE_LOGIN_SUCCESS: Byte = 0x12


        //从ycServer发送消息到yc客户端
        val SEND_YC_CLIENT_FROM_YC_SERVER: Byte = 0x20

        //从yc客户端 发送消息到ycServer
        val SEND_YC_SERVER_FROM_YC_CLIENT: Byte = 0x21


    }


    override fun toString(): String {
        return "Pandora(msgType=$msgType, channelId=$channelId, content=$content, data=${Arrays.toString(data)})"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Pandora

        if (msgType != other.msgType) return false
        if (channelId != other.channelId) return false
        if (content != other.content) return false
        if (!Arrays.equals(data, other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result: Int = msgType!!.toInt()
        result = 31 * result + (channelId?.hashCode() ?: 0)
        result = 31 * result + (content?.hashCode() ?: 0)
        result = 31 * result + (data?.let { Arrays.hashCode(it) } ?: 0)
        return result
    }

}


object Constant {

    val LENGTH_FILED = 4
    private val MSG_TYPE_LENGTH = 1
    private val CONTENT_LENGTH = 4
    val CHANNEL_ID_LENGTH = 60
    val HEADER_LENGTH = LENGTH_FILED + CONTENT_LENGTH + MSG_TYPE_LENGTH + CHANNEL_ID_LENGTH


}



object LengthFiledDecoder {

    val MAX_FRAME_LENGTH = 10 * 1024 * 1024

    val LENGTH_FIELD_OFFSET = 0

    val LENGTH_FIELD_LENGTH = 4

    val LENGTH_ADJUSTMENT = 0

    val INITIAL_BYTES_TO_STRIP = 4
}