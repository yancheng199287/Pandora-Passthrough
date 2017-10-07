package net.ftzcode.server

import io.netty.bootstrap.ServerBootstrap
import io.netty.buffer.PooledByteBufAllocator
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.LengthFieldBasedFrameDecoder
import io.netty.handler.codec.LengthFieldPrepender
import io.netty.handler.codec.http.HttpObjectAggregator
import net.ftzcode.protocol.LengthFiledDecoder
import net.ftzcode.protocol.YCMessageDecoder
import net.ftzcode.protocol.YCMessageEncoder
import net.ftzcode.server.handler.HttpServerHandler
import net.ftzcode.server.handler.YCServerHandler
import org.slf4j.LoggerFactory


/**
 * Created by WangZiHe on 2017/9/3.
 * QQ/WeChat:648830605
 * QQ-Group:368512253
 * Blog:http:www.520code.net
 * Github:https://github.com/yancheng199287
 */

class YCServer(private val httpPort: Int, private val serverPort: Int) {
    private val logger = LoggerFactory.getLogger("YCServer")

    fun startHttpService() {
        val bossGroup = NioEventLoopGroup()
        val workerGroup = NioEventLoopGroup()
        val serverBootstrap = ServerBootstrap()
        try {
            serverBootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel::class.java)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                   // .option(ChannelOption.TCP_NODELAY, true)
                  //  .option(ChannelOption.SO_TIMEOUT, 10000)
                  //  .option(ChannelOption.SO_KEEPALIVE, false)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                    .childHandler(
                            object : ChannelInitializer<SocketChannel>() {
                                override fun initChannel(ch: SocketChannel?) {
                                    ch!!.pipeline()
                                            .addLast(HttpServerHandler())
                                }
                            })
            val f: ChannelFuture = serverBootstrap.bind(httpPort).sync()
            val address = f.channel().localAddress()
            logger.info("Http Server has been started，address:$address ,  please enjoy it！")
            f.channel().closeFuture().sync()
        } finally {
            bossGroup.shutdownGracefully()
            workerGroup.shutdownGracefully()
            logger.warn("Http server has been shutdown， EventLoopGroup  has been released all resources！")
        }


    }


    fun startYCServer() {
        val bossGroup = NioEventLoopGroup()
        val workerGroup = NioEventLoopGroup()
        try {
            val serverBootstrap = ServerBootstrap()

            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel::class.java)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                  //  .option(ChannelOption.TCP_NODELAY, true)
                   // .option(ChannelOption.SO_TIMEOUT, 10000)
                   // .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                    //   .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .childHandler(object : ChannelInitializer<SocketChannel>() {
                        override fun initChannel(ch: SocketChannel?) {
                            ch!!.pipeline()
                                    .addLast(LengthFieldBasedFrameDecoder(LengthFiledDecoder.MAX_FRAME_LENGTH,LengthFiledDecoder.LENGTH_FIELD_OFFSET,
                                            LengthFiledDecoder.LENGTH_FIELD_LENGTH,LengthFiledDecoder.LENGTH_ADJUSTMENT,LengthFiledDecoder.INITIAL_BYTES_TO_STRIP,true))
                                    .addLast(YCMessageDecoder())
                                    .addLast(LengthFieldPrepender(4))
                                    .addLast(YCMessageEncoder())
                                    .addLast(YCServerHandler())
                        }
                    })
            val f: ChannelFuture = serverBootstrap.bind(serverPort).sync()
            f.channel().closeFuture().sync()
        } finally {
            bossGroup.shutdownGracefully()
            workerGroup.shutdownGracefully()
        }

    }
}

