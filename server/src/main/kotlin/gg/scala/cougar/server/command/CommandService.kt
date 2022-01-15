package gg.scala.cougar.server.command

import gg.scala.cougar.server.command.impl.SystemCommands
import gg.scala.flavor.service.Close
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import revxrsal.commands.cli.ConsoleCommandHandler
import kotlin.concurrent.thread

/**
 * @author GrowlyX
 * @since 1/15/2022
 */
@Service("Commands")
object CommandService
{
    private var started = false

    @Configure
    fun configure()
    {
        val commandHandler = ConsoleCommandHandler.create()
        commandHandler.register(SystemCommands)

        started = true

        thread {
            while (started)
            {
                try
                {
                    commandHandler.pollInput()
                } catch (e: Exception)
                {
                    e.printStackTrace()
                }
            }
        }
    }

    @Close
    fun close()
    {
        started = false
    }
}
