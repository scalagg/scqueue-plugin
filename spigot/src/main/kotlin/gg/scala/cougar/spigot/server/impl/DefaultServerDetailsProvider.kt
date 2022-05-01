package gg.scala.cougar.spigot.server.impl

import gg.scala.cougar.spigot.ScalaCougarSpigotPlugin
import gg.scala.cougar.spigot.server.ServerDetailsProvider
import org.bukkit.plugin.java.JavaPlugin

/**
 * @author GrowlyX
 * @since 1/15/2022
 */
object DefaultServerDetailsProvider : ServerDetailsProvider
{
    private var id: String

    init
    {
        val plugin =
            ScalaCougarSpigotPlugin.instance

        plugin.saveDefaultConfig()
        id = plugin.config.getString("id")
    }

    override fun getServerName() = id
}
