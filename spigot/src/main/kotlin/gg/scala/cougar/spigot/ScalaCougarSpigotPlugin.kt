package gg.scala.cougar.spigot

import gg.scala.cougar.spigot.protocol.ProtocolVersionProvider
import gg.scala.cougar.spigot.protocol.impl.CubedVersionProvider
import gg.scala.cougar.spigot.protocol.impl.ProtocolSupportProvider
import gg.scala.cougar.spigot.protocol.impl.ViaVersionProvider
import gg.scala.cougar.spigot.provider.PlayerPriorityProvider
import gg.scala.cougar.spigot.provider.impl.LemonPlayerPriorityProvider
import gg.scala.cougar.spigot.server.ServerDetailsProvider
import gg.scala.cougar.spigot.server.impl.DefaultServerDetailsProvider
import gg.scala.cougar.spigot.server.impl.LemonServerDetailsProvider
import gg.scala.flavor.Flavor
import gg.scala.flavor.FlavorOptions
import me.lucko.helper.plugin.ExtendedJavaPlugin
import me.lucko.helper.plugin.ap.Plugin
import me.lucko.helper.plugin.ap.PluginDependency
import org.bukkit.Bukkit

/**
 * @author GrowlyX
 * @since 1/12/2022
 */
@Plugin(
    name = "Cougar",
    depends = [
        PluginDependency("helper"),
        PluginDependency("store-spigot"),
        PluginDependency("Lemon", soft = true),
        PluginDependency("Cubed", soft = true),
    ]
)
class ScalaCougarSpigotPlugin : ExtendedJavaPlugin()
{
    lateinit var priorityProvider: PlayerPriorityProvider
    lateinit var protocolVersionProvider: ProtocolVersionProvider
    lateinit var serverDetailsProvider: ServerDetailsProvider

    override fun enable()
    {
        ScalaCougarSpigotEngine.flavor = Flavor
            .create<ScalaCougarSpigotPlugin>(
                FlavorOptions(logger)
            )

        registerProviders()

        ScalaCougarSpigotEngine.preLoad()

        ScalaCougarSpigotEngine.flavor.bind<ScalaCougarSpigotPlugin>() to this
        ScalaCougarSpigotEngine.flavor.inject(ScalaCougarSpigotEngine)

        ScalaCougarSpigotEngine.configure()
    }

    private fun registerProviders()
    {
        if ("Lemon".pluginExists())
        {
            priorityProvider = LemonPlayerPriorityProvider()
            serverDetailsProvider = LemonServerDetailsProvider()
        } else
        {
            serverDetailsProvider = DefaultServerDetailsProvider
                .also { it.configure(this) }
        }

        if ("ProtocolSupport".pluginExists())
        {
            protocolVersionProvider = ProtocolSupportProvider()
        } else if ("ViaVersion".pluginExists())
        {
            protocolVersionProvider = ViaVersionProvider()
        } else if ("Cubed".pluginExists())
        {
            protocolVersionProvider = CubedVersionProvider()
        }
    }

    private fun String.pluginExists(): Boolean
    {
        return Bukkit.getPluginManager().getPlugin(this) != null
    }

    override fun disable()
    {
        ScalaCougarSpigotEngine.close()
    }
}
