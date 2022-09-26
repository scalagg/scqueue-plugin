package gg.scala.cougar.spigot

import gg.scala.commons.ExtendedScalaPlugin
import gg.scala.commons.agnostic.sync.ServerSync
import gg.scala.commons.annotations.commands.ManualRegister
import gg.scala.commons.annotations.container.ContainerDisable
import gg.scala.commons.annotations.container.ContainerEnable
import gg.scala.commons.command.ScalaCommandManager
import gg.scala.cougar.spigot.command.impl.JoinQueueCommand
import gg.scala.cougar.spigot.protocol.ProtocolVersionProvider
import gg.scala.cougar.spigot.provider.PlayerPriorityProvider
import me.lucko.helper.plugin.ap.Plugin
import me.lucko.helper.plugin.ap.PluginDependency
import net.kyori.adventure.key.Key

/**
 * @author GrowlyX
 * @since 1/12/2022
 */
@Plugin(
    name = "ScQueue",
    depends = [
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

    @ContainerEnable
    fun containerEnable()
    {
        instance = this
        ScalaCougarSpigotEngine.preLoad()

        ServerSync.getLocalGameServer()
            .setMetadata(
                Key.key("queue", "joinable"),
                ServerSync.getLocalGameServer()
                    .id.lowercase().contains("hub")
            )

        flavor().inject(ScalaCougarSpigotEngine)
    }

    @ManualRegister
    fun manualRegister(
        manager: ScalaCommandManager
    )
    {
        val groups = ServerSync.getLocalGameServer().groups

        if (
            groups.contains("hub") ||
            groups.contains("lobby")
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
