package gg.scala.cougar.spigot.command

import net.evilblock.cubed.acf.ConditionFailedException
import net.evilblock.cubed.acf.PaperCommandManager
import gg.scala.cougar.common.queue.impl.wrapper.CougarQueueWrapper
import gg.scala.cougar.spigot.ScalaCougarSpigotEngine
import gg.scala.cougar.spigot.ScalaCougarSpigotPlugin
import gg.scala.cougar.spigot.command.impl.JoinQueueCommand
import gg.scala.cougar.spigot.command.impl.LeaveQueueCommand
import gg.scala.cougar.spigot.command.impl.administration.CougarCommand
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.lemon.Lemon
import net.evilblock.cubed.command.manager.CubedCommandManager
import org.bukkit.ChatColor

/**
 * @author GrowlyX
 * @since 1/12/2022
 */
@Service
object CommandService
{
    @Inject
    lateinit var plugin: ScalaCougarSpigotPlugin

    @Configure
    fun configure()
    {
        val engine = ScalaCougarSpigotEngine

        val manager = CubedCommandManager(
            plugin = plugin,
            primary = ChatColor.valueOf(Lemon.instance.lemonWebData.primary),
            secondary = ChatColor.valueOf(Lemon.instance.lemonWebData.secondary)
        )

        manager.commandContexts.registerContext(CougarQueueWrapper::class.java) {
            val poppedArgument = it.popFirstArg()

            return@registerContext engine.queueLayer.localCache()
                .values.firstOrNull { wrapper ->
                    wrapper.serverId.equals(poppedArgument, true)
                } ?: throw ConditionFailedException(
                    "There is no queue named ${ChatColor.YELLOW}$poppedArgument${ChatColor.RED}."
                )
        }

        val serverId = plugin.serverDetailsProvider
            .getServerName().lowercase()

        if (
            serverId.contains("hub") ||
            serverId.contains("lobby")
        )
        {
            manager.registerCommand(JoinQueueCommand)
        }

        engine.flavor.bind<ScalaCougarSpigotEngine>() to engine
        engine.flavor.inject(CougarCommand)

        manager.registerCommand(CougarCommand)
        manager.registerCommand(LeaveQueueCommand)
    }
}
