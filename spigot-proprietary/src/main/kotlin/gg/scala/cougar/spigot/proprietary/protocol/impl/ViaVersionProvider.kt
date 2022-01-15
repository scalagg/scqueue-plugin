package gg.scala.cougar.spigot.proprietary.protocol.impl

import com.viaversion.viaversion.api.Via
import gg.scala.cougar.spigot.proprietary.protocol.ProtocolVersionProvider
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 1/15/2022
 */
class ViaVersionProvider : ProtocolVersionProvider
{
    override fun getProtocolVersion(player: Player): Int
    {
        return Via.getAPI().getPlayerVersion(player)
    }
}
