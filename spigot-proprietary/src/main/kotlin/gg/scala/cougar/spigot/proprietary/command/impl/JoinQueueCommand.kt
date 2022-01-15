package gg.scala.cougar.spigot.proprietary.command.impl

import gg.scala.cougar.common.message.ScCougarServerMessageProducer
import gg.scala.cougar.common.queue.impl.wrapper.CougarQueueWrapper
import gg.scala.cougar.spigot.proprietary.ScalaCougarSpigotProprietaryEngine
import gg.scala.cougar.spigot.proprietary.command.CommandService
import co.aikar.commands.BaseCommand
import co.aikar.commands.ConditionFailedException
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import org.bukkit.ChatColor
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 1/12/2022
 */
object JoinQueueCommand : BaseCommand()
{
    @CommandAlias("joinq|joinqueue|queue")
    @CommandPermission("cougar.command.joinqueue")
    fun onJoinQueue(player: Player, server: CougarQueueWrapper)
    {
        val engine = ScalaCougarSpigotProprietaryEngine

        if (
            engine.queueLayer.localCache.values.any {
                it.isInQueue(player.uniqueId)
            }
        )
        {
            throw ConditionFailedException("You're already queued for a server.")
        }

        val pVersion = CommandService.plugin
            .protocolVersionProvider
            .getProtocolVersion(player)

        if (!server.allowedProtocols.contains(pVersion))
        {
            throw ConditionFailedException("You can only join this queue if you're on: ${
                server.allowedProtocols
                    .joinToString {
                        CommandService.plugin
                            .protocolVersionProvider
                            .findFancyMapping(it)
                    }
            }.")
        }

        ScCougarServerMessageProducer.produce(
            "queue-add",
            "serverId" to server.serverId,
            "uniqueId" to player.uniqueId
        ).dispatch(
            ScCougarServerMessageProducer.banana
        )

        player.sendMessage("${ChatColor.GREEN}You've been added to the ${ChatColor.YELLOW}${server.fancyDisplayName}${ChatColor.GREEN} queue.")
    }
}
