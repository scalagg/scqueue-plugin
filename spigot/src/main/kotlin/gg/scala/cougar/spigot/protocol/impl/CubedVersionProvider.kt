package gg.scala.cougar.spigot.protocol.impl

import gg.scala.cougar.spigot.protocol.ProtocolVersionProvider
import net.evilblock.cubed.util.nms.MinecraftProtocol
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 1/15/2022
 */
class CubedVersionProvider : ProtocolVersionProvider
{
    override fun getProtocolVersion(player: Player): Int
    {
        return MinecraftProtocol.getPlayerVersion(player)
    }
}
