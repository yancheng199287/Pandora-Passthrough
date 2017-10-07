package net.ftzcode.client.util

import java.util.*

/**
 *  Created by WangZiHe on 2017/8/29.
 * QQ/WeChat:648830605
 * QQ-Group:368512253
 * Blog:www.520code.net
 */

object AppConf {

    private val map = HashMap<String, String>(5)

    val remoteHost = getStringValue()["remoteHost"]
    val remotePort = getStringValue()["remotePort"]!!.toInt()
    val localHost = getStringValue()["localHost"]
    val localServerPort = getStringValue()["localServerPort"]!!.toInt()
    val accessKey = getStringValue()["accessKey"]!!



    private fun getStringValue(): HashMap<String, String> {
        if (map.size <= 0) {
            val resourceBundle = ResourceBundle.getBundle("appConf")
            resourceBundle.keySet().forEach {
                run {
                    val value = resourceBundle.getString(it)
                    map.put(it, value)
                    println("current config params：key:$it , value:$value")
                }
            }
        }
        return map
    }

}