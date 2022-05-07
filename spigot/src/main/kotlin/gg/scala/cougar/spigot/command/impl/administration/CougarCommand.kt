package gg.scala.cougar.spigot.command.impl.administration

import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.cougar.spigot.ScalaCougarSpigotEngine
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Service
import gg.scala.commons.acf.BaseCommand
import gg.scala.commons.acf.CommandHelp
import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.acf.annotation.CommandPermission
import gg.scala.commons.acf.annotation.Default
import gg.scala.commons.acf.annotation.Description
import gg.scala.commons.acf.annotation.HelpCommand
import gg.scala.commons.acf.annotation.Subcommand
import net.evilblock.cubed.util.CC
import org.bukkit.command.CommandSender

/**
 * @author GrowlyX
 * @since 4/8/2022
 */
@AutoRegister
@CommandAlias("cougar|cgr")
@CommandPermission("cougar.command.cougar")
object CougarCommand : ScalaCommand()
{
    @Inject
    lateinit var engine: ScalaCougarSpigotEngine

    @Default
    @HelpCommand
    fun onHelp(
        help: CommandHelp
    )
    {
        help.showHelp()
    }

    @Subcommand("queues")
    @Description("Show a list of registered queues.")
    fun onQueues(
        sender: CommandSender
    )
    {
        val queues = engine
            .queueLayer.localCache()

        sender.sendMessage("")
        sender.sendMessage("${CC.B_PRI}Available Queues:")
        sender.sendMessage(" Listed: ${CC.SEC}${queues.size}")
        sender.sendMessage("")

        for (queue in queues)
        {
            sender.sendMessage(" ${queue.value.serverId} ${CC.GRAY}(${queue.value.fancyDisplayName})")
        }

        sender.sendMessage("")
    }
}
