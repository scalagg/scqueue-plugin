package gg.scala.cougar.server.command.impl

import gg.scala.cougar.server.console.ConsoleChatColor
import gg.scala.cougar.server.settings.CougarSettingsProcessor
import gg.scala.cougar.server.settings.impl.CougarIterationSettings
import gg.scala.cougar.server.settings.impl.CougarLanguageSettings
import gg.scala.cougar.server.settings.impl.CougarQueueSettings
import revxrsal.commands.annotation.Command
import revxrsal.commands.cli.ConsoleActor
import java.lang.management.ManagementFactory
import kotlin.math.pow

/**
 * @author GrowlyX
 * @since 1/15/2022
 */
object SystemCommands
{
    @Command("reload-config")
    fun onReloadConfig(actor: ConsoleActor)
    {
        val start = System.currentTimeMillis()

        CougarSettingsProcessor.loadSettings<CougarLanguageSettings>("language")
        CougarSettingsProcessor.loadSettings<CougarQueueSettings>("queues")
        CougarSettingsProcessor.loadSettings<CougarIterationSettings>("iteration")

        actor.reply("${ConsoleChatColor.GREEN}Finished reload in ${ConsoleChatColor.YELLOW}${System.currentTimeMillis() - start}ms${ConsoleChatColor.GREEN}.")
    }

    @Command("tps")
    fun onTps(actor: ConsoleActor)
    {
        actor.reply("${ConsoleChatColor.RED}So, we don't have a TPS, but here's memory usage!")
        actor.reply("${ConsoleChatColor.GREEN_BOLD}${
            (ManagementFactory.getMemoryMXBean().heapMemoryUsage.used / (1024.0.pow(2.0))).toInt()
        }mb")
    }

    @Command("queues")
    fun onQueues(actor: ConsoleActor)
    {
        CougarSettingsProcessor.locate<CougarQueueSettings>()!!.apply {
            actor.reply("${
                ConsoleChatColor.YELLOW
            }Queues: ${
                ConsoleChatColor.CYAN_BOLD_BRIGHT
            }${
                queues.joinToString { it.fancyDisplayName + " (${it.targetServerId})" }
            }${
                ConsoleChatColor.YELLOW
            }.")
        }
    }
}
