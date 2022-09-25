package gg.scala.cougar.spigot

import gg.scala.commons.ExtendedScalaPlugin
import gg.scala.commons.annotations.commands.ManualRegister
import gg.scala.commons.annotations.container.ContainerDisable
import gg.scala.commons.annotations.container.ContainerEnable
import gg.scala.commons.command.ScalaCommandManager
import gg.scala.cougar.spigot.command.impl.JoinQueueCommand
import gg.scala.cougar.spigot.protocol.ProtocolVersionProvider
import gg.scala.cougar.spigot.provider.PlayerPriorityProvider
import gg.scala.cougar.spigot.server.ServerDetailsProvider
import gg.scala.cougar.spigot.server.impl.DefaultServerDetailsProvider
import me.lucko.helper.plugin.ap.Plugin
import me.lucko.helper.plugin.ap.PluginDependency

/**
 * @author GrowlyX
 * @since 1/12/2022
 */
@Plugin(
    name = "Cougar",
    depends = [
        PluginDependency("store-spigot"),
        PluginDependency("scala-commons"),
        PluginDependency("Lemon", soft = true),
        PluginDependency("cloudsync", soft = true),
        PluginDependency("ViaVersion", soft = true),
        PluginDependency("ProtocolSupport", soft = true)
    ]
)
class ScalaCougarSpigotPlugin : ExtendedScalaPlugin()
{
    companion object
    {
        @JvmStatic
        lateinit var instance: ScalaCougarSpigotPlugin
    }

    lateinit var priorityProvider: PlayerPriorityProvider
    lateinit var protocolVersionProvider: ProtocolVersionProvider

    var serverDetailsProvider: ServerDetailsProvider =
        DefaultServerDetailsProvider

    @ContainerEnable
    fun containerEnable()
    {
        instance = this
        ScalaCougarSpigotEngine.preLoad()

        flavor().inject(ScalaCougarSpigotEngine)
    }

    @ManualRegister
    fun manualRegister(
        manager: ScalaCommandManager
    )
    {
        val serverId = serverDetailsProvider
            .getServerName().lowercase()

        if (
            serverId.contains("hub") ||
            serverId.contains("lobby")
        )
        {
            flavor().inject(JoinQueueCommand)

            manager.registerCommand(JoinQueueCommand)
        }
    }

    @ContainerDisable
    fun containerDisable()
    {
        ScalaCougarSpigotEngine.close()
    }
}
