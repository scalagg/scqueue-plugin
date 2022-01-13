package gg.scala.cougar.spigot.proprietary.command.impl

import gg.scala.cougar.common.queue.impl.wrapper.CougarQueueWrapper
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
        if (wrapper.isInQueue(player.uniqueId))
        {
            throw ConditionFailedException("You're already queued for ${CC.YELLOW}${wrapper.fancyDisplayName}${CC.RED}.")
        }


    }
}
