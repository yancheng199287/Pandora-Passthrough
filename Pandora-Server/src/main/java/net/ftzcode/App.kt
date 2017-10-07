package net.ftzcode

import net.ftzcode.server.YCServer
import net.ftzcode.server.util.AppConf

/**
 * Created by WangZiHe on 2017/10/6
 * QQ/WeChat:648830605
 * QQ-Group:368512253
 * Blog:www.520code.net
 * Github:https://github.com/yancheng199287
 */


fun main(args: Array<String>) {
    println("Hello,welcome to use Pandora  Pass Through, please just enjoy it, if you have any questionn,please  call me by 648830605@qq.com")
    println("starting  YC-server...")

    val server = YCServer(AppConf.httpPort, AppConf.serverPort)
    Thread(Runnable { -> server.startHttpService() }).start()
    Thread(Runnable { -> server.startYCServer() }).start()

}