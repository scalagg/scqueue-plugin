package gg.scala.cougar.spigot.command.impl

import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.cougar.common.message.ScCougarServerMessageProducer
import gg.scala.cougar.spigot.ScalaCougarSpigotEngine
import gg.scala.commons.acf.ConditionFailedException
import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.acf.annotation.CommandPermission
import org.bukkit.ChatColor
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 1/12/2022
 */
@AutoRegister
object LeaveQueueCommand : ScalaCommand()
{
    @CommandAlias("leaveq|leavequeue")
    @CommandPermission("scqueue.command.leavequeue")
    fun onJoinQueue(player: Player)
    {
        val engine = ScalaCougarSpigotEngine

        val queued = engine.queueLayer.localCache().values
            .firstOrNull { it.isInQueue(player.uniqueId) }
            ?: throw ConditionFailedException("You're not currently queued for a server.")

        ScCougarServerMessageProducer.produce(
            "queue-remove",
            "uniqueId" to player.uniqueId
        ).publish()

        player.sendMessage("${ChatColor.RED}You've left ${ChatColor.YELLOW}${queued.fancyDisplayName}${ChatColor.RED} queue.")
    }
}
