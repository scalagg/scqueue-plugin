package gg.scala.cougar.spigot.proprietary.protocol.impl

import gg.scala.cougar.spigot.proprietary.protocol.ProtocolVersionProvider
import org.bukkit.entity.Player
import protocolsupport.api.ProtocolSupportAPI

/**
 * @author GrowlyX
 * @since 1/15/2022
 */
class ProtocolSupportProvider : ProtocolVersionProvider
{
    override fun getProtocolVersion(player: Player): Int
    {
        return ProtocolSupportAPI.getProtocolVersion(player).id
    }
}
