package gg.scala.cougar.spigot.protocol.impl

import com.viaversion.viaversion.api.Via
import gg.scala.commons.annotations.plugin.SoftDependency
import gg.scala.cougar.spigot.ScalaCougarSpigotPlugin
import gg.scala.cougar.spigot.protocol.ProtocolVersionProvider
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.flavor.service.ignore.IgnoreAutoScan
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 1/15/2022
 */
@Service
@IgnoreAutoScan
@SoftDependency("ViaVersion")
object ViaVersionProvider : ProtocolVersionProvider
{
    @Configure
    fun configure()
    {
        ScalaCougarSpigotPlugin
            .instance.protocolVersionProvider = this
    }

    override fun getProtocolVersion(player: Player): Int
    {
        return Via.getAPI().getPlayerVersion(player)
    }
}
