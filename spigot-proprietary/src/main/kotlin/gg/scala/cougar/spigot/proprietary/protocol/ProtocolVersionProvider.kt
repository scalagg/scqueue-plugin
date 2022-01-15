package gg.scala.cougar.spigot.proprietary.protocol

import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 1/15/2022
 */
interface ProtocolVersionProvider
{
    fun getProtocolVersion(player: Player): Int
}
