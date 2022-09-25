package gg.scala.cougar.spigot.server.impl

import gg.scala.commons.agnostic.sync.ServerSync
import gg.scala.commons.annotations.plugin.SoftDependency
import gg.scala.cougar.spigot.ScalaCougarSpigotPlugin
import gg.scala.cougar.spigot.server.ServerDetailsProvider
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.flavor.service.ignore.IgnoreAutoScan
import net.kyori.adventure.key.Key
import org.bukkit.plugin.java.JavaPlugin

/**
 * @author GrowlyX
 * @since 1/15/2022
 */
@Service
@SoftDependency("scala-commons")
@IgnoreAutoScan
object ServerSyncServerDetailsProvider : ServerDetailsProvider
{
    @Configure
    fun configure()
    {
        ScalaCougarSpigotPlugin
            .instance.serverDetailsProvider = this
    }

    override fun getServerName() = ServerSync.getLocalGameServer().id

    override fun updateMetaData(plugin: JavaPlugin)
    {
        ServerSync.getLocalGameServer()
            .setMetadata(
                Key.key("queue", "joinable"),
                getServerName().lowercase().contains("hub")
            )
    }
}
