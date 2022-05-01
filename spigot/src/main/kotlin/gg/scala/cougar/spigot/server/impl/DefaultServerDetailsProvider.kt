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
    private val id by lazy {
        val plugin =
            ScalaCougarSpigotPlugin.instance

        plugin.saveDefaultConfig()
        plugin.config.getString("id")
    }

    override fun getServerName(): String = id
}
