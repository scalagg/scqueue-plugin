package gg.scala.cougar.spigot.server.impl

import gg.scala.commons.annotations.plugin.SoftDependency
import gg.scala.cougar.spigot.ScalaCougarSpigotPlugin
import gg.scala.cougar.spigot.protocol.ProtocolVersionProvider
import gg.scala.cougar.spigot.server.ServerDetailsProvider
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.flavor.service.ignore.IgnoreAutoScan
import gg.scala.lemon.Lemon
import gg.scala.lemon.LemonConstants
import org.bukkit.plugin.java.JavaPlugin

/**
 * @author GrowlyX
 * @since 1/15/2022
 */
@Service
@IgnoreAutoScan
object LemonServerDetailsProvider : ServerDetailsProvider
{
    @Configure
    fun configure()
    {
        ScalaCougarSpigotPlugin
            .instance.serverDetailsProvider = this
    }

    override fun getServerName() = Lemon.instance.settings.id

    override fun updateMetaData(plugin: JavaPlugin)
    {
        Lemon.instance.localInstance
            .metaData["joinable"] = getServerName()
                .lowercase().contains("hub").toString()
    }
}
