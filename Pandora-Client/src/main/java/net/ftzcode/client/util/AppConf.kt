package net.ftzcode.client.util

import java.io.BufferedInputStream
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.util.*

/**
 *  Created by WangZiHe on 2017/8/29.
 * QQ/WeChat:648830605
 * QQ-Group:368512253
 * Blog:www.520code.net
 */

object AppConf {

    private val separator = System.getProperty("file.separator")

    private val map = HashMap<String, String>(5)

    val remoteHost = getStringValue()["remoteHost"]
    val remotePort = getStringValue()["remotePort"]!!.toInt()
    val localHost = getStringValue()["localHost"]
    val localServerPort = getStringValue()["localServerPort"]!!.toInt()
    val accessKey = getStringValue()["accessKey"]!!


    private fun getStringValue(): HashMap<String, String> {
        if (map.size <= 0) {
            try {
                ResourceBundle.getBundle("client")
                println("Loading conf file from program path...")
                getClassPathConf()
            } catch (e: MissingResourceException) {
                println("Loading conf file from program path...")
                getCurrentConf()
            }
        }
        return map
    }


    private fun getClassPathConf(): HashMap<String, String> {
        val resourceBundle = ResourceBundle.getBundle("client")
        resourceBundle.keySet().forEach {
            run {
                val value = resourceBundle.getString(it)
                map.put(it, value)
                println("current config params：key:$it , value:$value")
            }
        }
        return map
    }


    private fun getCurrentConf() {
        val path = System.getProperty("user.dir") + separator + "client.properties"
        var resourceBundle: ResourceBundle? = null
        try {
            BufferedInputStream(FileInputStream(path)).use { inputStream -> resourceBundle = PropertyResourceBundle(inputStream) }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val iterator = resourceBundle!!.keySet().iterator()
        while (iterator.hasNext()) {
            val it = iterator.next()
            val value = resourceBundle!!.getString(it)
            map.put(it, value)
            println("current config params：key:$it , value:$value")
        }
    }
}