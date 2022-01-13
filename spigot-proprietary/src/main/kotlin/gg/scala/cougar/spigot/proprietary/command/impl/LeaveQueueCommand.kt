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

        if (
            engine.queueLayer.localCache.values.none {
                it.isInQueue(player.uniqueId)
            }
        )
        {
            throw ConditionFailedException("You're not currently queued for a server.")
        }

        ScCougarServerMessageProducer.produce(
            "queue-remove",
            "uniqueId" to player.uniqueId
        ).dispatch(
            ScCougarServerMessageProducer.banana
        )

        // the ${CC.YELLOW}${
        //            wrapper.fancyDisplayName
        //        }${CC.RED}
        player.sendMessage("${CC.RED}You've left queue.")
    }
}
