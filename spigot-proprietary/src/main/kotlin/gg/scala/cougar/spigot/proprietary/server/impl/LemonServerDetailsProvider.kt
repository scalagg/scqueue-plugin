package gg.scala.cougar.spigot.proprietary.server.impl

import gg.scala.cougar.spigot.proprietary.server.ServerDetailsProvider
import gg.scala.lemon.Lemon
import gg.scala.lemon.LemonConstants
import org.bukkit.plugin.java.JavaPlugin

/**
 * @author GrowlyX
 * @since 1/15/2022
 */
class LemonServerDetailsProvider : ServerDetailsProvider
{
    override fun getServerName() = LemonConstants.SERVER_NAME

    override fun updateMetaData(plugin: JavaPlugin)
    {
        if (!getServerName().lowercase().contains("hub"))
        {
            Lemon.instance.localInstance
                .metaData["joinable"] = "false"
        }
    }
}
