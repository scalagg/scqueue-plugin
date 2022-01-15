package gg.scala.cougar.spigot.provider.impl

import gg.scala.cougar.spigot.provider.PlayerPriorityProvider
import gg.scala.lemon.handler.PlayerHandler
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 1/15/2022
 */
class LemonPlayerPriorityProvider : PlayerPriorityProvider
{
    override fun getPriority(player: Player): Int
    {
        return PlayerHandler.findPlayer(player.uniqueId)
            .orElse(null)?.let {
                it.activeGrant!!.getRank().weight
            } ?: 0
    }
}
