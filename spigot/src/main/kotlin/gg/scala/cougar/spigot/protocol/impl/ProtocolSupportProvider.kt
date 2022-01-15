package gg.scala.cougar.spigot.protocol.impl

import gg.scala.cougar.spigot.protocol.ProtocolVersionProvider
import org.bukkit.entity.Player
import protocolsupport.api.ProtocolSupportAPI
import protocolsupport.api.ProtocolVersion

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

    override fun findFancyMapping(int: Int): String
    {
        return ProtocolVersion.values()
            .first { it.id == int }.name
    }
}
