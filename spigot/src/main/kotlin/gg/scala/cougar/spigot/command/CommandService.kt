package gg.scala.cougar.spigot.command

import co.aikar.commands.ConditionFailedException
import co.aikar.commands.PaperCommandManager
import gg.scala.cougar.common.queue.impl.wrapper.CougarQueueWrapper
import gg.scala.cougar.spigot.ScalaCougarSpigotProprietaryPlugin
import gg.scala.cougar.spigot.command.impl.JoinQueueCommand
import gg.scala.cougar.spigot.command.impl.LeaveQueueCommand
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import org.bukkit.ChatColor

/**
 * @author GrowlyX
 * @since 1/12/2022
 */
@Service
object CommandService
{
    @Inject
    lateinit var plugin: ScalaCougarSpigotProprietaryPlugin

    @Configure
    fun configure()
    {
        val engine = gg.scala.cougar.spigot.ScalaCougarSpigotEngine
        val manager = PaperCommandManager(plugin)

        manager.commandContexts.registerContext(CougarQueueWrapper::class.java) {
            val poppedArgument = it.popFirstArg()

            return@registerContext engine.queueLayer
                .localCache.values.firstOrNull { wrapper ->
                    wrapper.serverId.equals(poppedArgument, true)
                } ?: throw ConditionFailedException(
                    "There is no queue named ${ChatColor.YELLOW}$poppedArgument${ChatColor.RED}."
                )
        }

        val serverId = plugin.serverDetailsProvider.getServerName()

        if (serverId.lowercase().contains("hub"))
        {
            manager.registerCommand(JoinQueueCommand)
        }

        manager.registerCommand(LeaveQueueCommand)
    }
}
