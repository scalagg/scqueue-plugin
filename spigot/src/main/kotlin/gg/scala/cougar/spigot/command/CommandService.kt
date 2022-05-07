package gg.scala.cougar.spigot.command

import gg.scala.commons.acf.ConditionFailedException
import gg.scala.commons.annotations.commands.customizer.CommandManagerCustomizer
import gg.scala.commons.command.ScalaCommandManager
import gg.scala.cougar.common.queue.impl.wrapper.CougarQueueWrapper
import gg.scala.cougar.spigot.ScalaCougarSpigotEngine
import org.bukkit.ChatColor

/**
 * @author GrowlyX
 * @since 1/12/2022
 */
object CommandService
{
    @CommandManagerCustomizer
    fun customize(
        manager: ScalaCommandManager
    )
    {
        val engine = ScalaCougarSpigotEngine

        manager.commandContexts.registerContext(CougarQueueWrapper::class.java) {
            val poppedArgument = it.popFirstArg()

            return@registerContext engine.queueLayer.localCache()
                .values.firstOrNull { wrapper ->
                    wrapper.serverId.equals(poppedArgument, true)
                } ?: throw ConditionFailedException(
                "There is no queue named ${ChatColor.YELLOW}$poppedArgument${ChatColor.RED}."
            )
        }
    }
}
