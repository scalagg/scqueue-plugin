package gg.scala.cougar.spigot.provider

import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 1/15/2022
 */
interface PlayerPriorityProvider
{
    fun getPriority(player: Player): Int
}
