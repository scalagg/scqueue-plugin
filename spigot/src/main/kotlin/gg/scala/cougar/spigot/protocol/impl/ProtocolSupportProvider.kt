package gg.scala.cougar.spigot.protocol.impl

import gg.scala.commons.annotations.plugin.SoftDependency
import gg.scala.cougar.spigot.ScalaCougarSpigotPlugin
import gg.scala.cougar.spigot.protocol.ProtocolVersionProvider
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.flavor.service.ignore.IgnoreAutoScan
import org.bukkit.entity.Player
import protocolsupport.api.ProtocolSupportAPI
import protocolsupport.api.ProtocolVersion

/**
 * @author GrowlyX
 * @since 1/15/2022
 */
@Service
@IgnoreAutoScan
@SoftDependency("ProtocolSupport")
object ProtocolSupportProvider : ProtocolVersionProvider
{
    @Configure
    fun configure()
    {
        ScalaCougarSpigotPlugin
            .instance.protocolVersionProvider = this
    }

    override fun getProtocolVersion(player: Player): Int
    {
        return ProtocolSupportAPI.getProtocolVersion(player).id
    }

    override fun findFancyMapping(int: Int): String
    {
        return ProtocolVersion.values()
            .first { it.id == int }.name
    }
}
