package gg.scala.cougar.spigot.server.impl

import gg.scala.cougar.spigot.server.ServerDetailsProvider
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
        Lemon.instance.localInstance
            .metaData["joinable"] = getServerName()
                .lowercase().contains("hub").toString()
    }
}
