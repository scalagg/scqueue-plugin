package gg.scala.cougar.spigot.proprietary.command.impl

import gg.scala.cougar.common.message.ScCougarServerMessageProducer
import gg.scala.cougar.spigot.proprietary.ScalaCougarSpigotProprietaryEngine
import net.evilblock.cubed.acf.BaseCommand
import net.evilblock.cubed.acf.ConditionFailedException
import net.evilblock.cubed.acf.annotation.CommandAlias
import net.evilblock.cubed.acf.annotation.CommandPermission
import org.bukkit.ChatColor
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 1/12/2022
 */
object LeaveQueueCommand : BaseCommand()
{
    @CommandAlias("queuetest")
    @CommandPermission("op")
    fun onTest(player: Player)
    {
        player.sendMessage(
            ScalaCougarSpigotProprietaryEngine.queueLayer.localCache.values.joinToString { it.fancyDisplayName }
        )
    }

    @CommandAlias("leaveq|leavequeue")
    @CommandPermission("cougar.command.leavequeue")
    fun onJoinQueue(player: Player)
    {
        val engine = ScalaCougarSpigotProprietaryEngine

        val queued = engine.queueLayer.localCache.values
            .firstOrNull { it.isInQueue(player.uniqueId) }
            ?: throw ConditionFailedException("You're not currently queued for a server.")

        ScCougarServerMessageProducer.produce(
            "queue-remove",
            "uniqueId" to player.uniqueId
        ).dispatch(
            ScCougarServerMessageProducer.banana
        )

        player.sendMessage("${ChatColor.RED}You've left ${ChatColor.YELLOW}${queued.fancyDisplayName}${ChatColor.RED} queue.")
    }
}
