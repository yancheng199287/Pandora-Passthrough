package net.ftzcode

import net.ftzcode.server.YCServer
import net.ftzcode.server.util.AppConf
import org.slf4j.LoggerFactory

/**
 * Created by WangZiHe on 2017/10/6
 * QQ/WeChat:648830605
 * QQ-Group:368512253
 * Blog:www.520code.net
 * Github:https://github.com/yancheng199287
 */


fun main(args: Array<String>) {


    val path = System.getProperty("user.dir")

    println("start Pandora-Server...,  your program from $path")


    println("Hello,welcome to use Pandora-Passthrough, please just enjoy it, if you have any question,please call me!")

    println(" \n" +
            " * QQ/WeChat:648830605\n" +
            " * QQ-Group:368512253\n" +
            " * Blog:www.520code.net\n" +
            " * Github:https://github.com/yancheng199287")

    val server = YCServer(AppConf.httpPort, AppConf.serverPort)
    Thread(Runnable { -> server.startHttpService() }).start()
    Thread(Runnable { -> server.startYCServer() }).start()


}