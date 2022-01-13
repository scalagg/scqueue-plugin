package gg.scala.cougar.spigot.proprietary.command.impl

import gg.scala.cougar.common.message.ScCougarServerMessageProducer
import gg.scala.cougar.common.queue.impl.wrapper.CougarQueueWrapper
import gg.scala.cougar.spigot.proprietary.ScalaCougarSpigotProprietaryEngine
import net.evilblock.cubed.acf.BaseCommand
import net.evilblock.cubed.acf.ConditionFailedException
import net.evilblock.cubed.acf.annotation.CommandAlias
import net.evilblock.cubed.acf.annotation.CommandPermission
import net.evilblock.cubed.util.CC
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 1/12/2022
 */
object JoinQueueCommand : BaseCommand()
{
    @CommandAlias("joinq|joinqueue|queue")
    @CommandPermission("cougar.command.joinqueue")
    fun onJoinQueue(player: Player, wrapper: CougarQueueWrapper)
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

        ScCougarServerMessageProducer.produce(
            "queue-add",
            "serverId" to wrapper.serverId,
            "uniqueId" to player.uniqueId
        ).dispatch(
            ScCougarServerMessageProducer.banana
        )

        player.sendMessage("${CC.GREEN}You've been added to the ${CC.YELLOW}${
            wrapper.fancyDisplayName
        }${CC.GREEN} queue.")
    }
}
