package gg.scala.cougar.spigot.proprietary.server

import org.bukkit.plugin.java.JavaPlugin

/**
 * @author GrowlyX
 * @since 1/15/2022
 */
interface ServerDetailsProvider
{
    fun getServerName(): String

    fun updateMetaData(plugin: JavaPlugin)
    {

    }
}
