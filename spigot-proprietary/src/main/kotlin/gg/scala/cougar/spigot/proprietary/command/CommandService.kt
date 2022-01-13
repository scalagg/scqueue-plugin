package gg.scala.cougar.spigot.proprietary.command

import gg.scala.cougar.common.queue.impl.wrapper.CougarQueueWrapper
import gg.scala.cougar.spigot.proprietary.ScalaCougarSpigotProprietaryEngine
import gg.scala.cougar.spigot.proprietary.ScalaCougarSpigotProprietaryPlugin
import gg.scala.cougar.spigot.proprietary.command.impl.JoinQueueCommand
import gg.scala.cougar.spigot.proprietary.command.impl.LeaveQueueCommand
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.lemon.Lemon
import net.evilblock.cubed.acf.ConditionFailedException
import net.evilblock.cubed.command.manager.CubedCommandManager
import net.evilblock.cubed.util.CC
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
        val engine = ScalaCougarSpigotProprietaryEngine

        val manager = CubedCommandManager(
            plugin = plugin,
            primary = ChatColor.valueOf(Lemon.instance.lemonWebData.primary),
            secondary = ChatColor.valueOf(Lemon.instance.lemonWebData.secondary)
        )

        manager.commandContexts.registerContext(CougarQueueWrapper::class.java) {
            val poppedArgument = it.popFirstArg()

            return@registerContext engine.queueLayer
                .localCache.values.firstOrNull { wrapper ->
                    wrapper.serverId.equals(poppedArgument, true)
                } ?: throw ConditionFailedException(
                    "There is no queue named ${CC.YELLOW}$poppedArgument${CC.RED}."
                )
        }

        Lemon.instance.registerCompletionsAndContexts(manager)

        manager.registerCommand(JoinQueueCommand)
        manager.registerCommand(LeaveQueueCommand)
    }
}
