package gg.scala.cougar.spigot.command.impl

import com.google.common.cache.CacheBuilder
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.cougar.common.message.ScCougarServerMessageProducer
import gg.scala.cougar.common.queue.impl.wrapper.CougarQueueWrapper
import gg.scala.cougar.spigot.command.CommandService
import gg.scala.commons.acf.BaseCommand
import gg.scala.commons.acf.ConditionFailedException
import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.acf.annotation.CommandPermission
import gg.scala.cougar.spigot.ScalaCougarSpigotEngine
import gg.scala.cougar.spigot.ScalaCougarSpigotPlugin
import gg.scala.flavor.inject.Inject
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * @author GrowlyX
 * @since 1/12/2022
 */
object JoinQueueCommand : ScalaCommand()
{
    @Inject
    lateinit var plugin: ScalaCougarSpigotPlugin

    private val coolDown = CacheBuilder.newBuilder()
        .expireAfterWrite(1L, TimeUnit.SECONDS)
        .build<UUID, Long>()

    @CommandAlias("joinq|joinqueue|queue")
    @CommandPermission("cougar.command.joinqueue")
    fun onJoinQueue(player: Player, server: CougarQueueWrapper)
    {
        val engine = ScalaCougarSpigotEngine

        val coolDown = coolDown
            .getIfPresent(player.uniqueId)

        if (coolDown != null)
        {
            throw ConditionFailedException("Please wait a moment before joining another queue.")
        }

        this.coolDown.put(
            player.uniqueId,
            System.currentTimeMillis()
        )

        if (
            engine.queueLayer.localCache().values.any {
                it.isInQueue(player.uniqueId)
            }
        )
        {
            throw ConditionFailedException("You're already queued for a server.")
        }

        val pVersion = plugin
            .protocolVersionProvider
            .getProtocolVersion(player)

        if (!server.allowedProtocols.contains(pVersion))
        {
            throw ConditionFailedException("You can only join this queue if you're on: ${
                server.allowedProtocols
                    .joinToString {
                        plugin
                            .protocolVersionProvider
                            .findFancyMapping(it)
                    }
            }.")
        }

        ScCougarServerMessageProducer.produce(
            "queue-add",
            "serverId" to server.serverId,
            "uniqueId" to player.uniqueId
        ).publish()

        player.sendMessage("${ChatColor.GREEN}You've been added to the ${ChatColor.YELLOW}${server.fancyDisplayName}${ChatColor.GREEN} queue.")
    }
}
