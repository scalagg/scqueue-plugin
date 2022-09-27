package gg.scala.cougar.spigot.provider.impl

import gg.scala.commons.annotations.plugin.SoftDependency
import gg.scala.cougar.spigot.ScalaCougarSpigotPlugin
import gg.scala.cougar.spigot.provider.PlayerPriorityProvider
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.flavor.service.ignore.IgnoreAutoScan
import gg.scala.lemon.handler.PlayerHandler
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 1/15/2022
 */
@Service
@IgnoreAutoScan
@SoftDependency("Lemon")
object LemonPlayerPriorityProvider : PlayerPriorityProvider
{
    @Configure
    fun configure()
    {
        ScalaCougarSpigotPlugin
            .instance.priorityProvider = this
    }

    override fun getPriority(player: Player): Int
    {
        return PlayerHandler.findPlayer(player.uniqueId)
            .orElse(null)?.let {
                it.activeGrant!!.getRank().weight
            } ?: 0
    }
}
