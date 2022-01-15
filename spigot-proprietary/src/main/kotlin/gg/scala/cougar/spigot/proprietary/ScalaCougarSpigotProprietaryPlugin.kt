package gg.scala.cougar.spigot.proprietary

import gg.scala.cougar.spigot.proprietary.backbone.BackboneService
import gg.scala.cougar.spigot.proprietary.protocol.ProtocolVersionProvider
import gg.scala.cougar.spigot.proprietary.protocol.impl.CubedVersionProvider
import gg.scala.cougar.spigot.proprietary.protocol.impl.ProtocolSupportProvider
import gg.scala.cougar.spigot.proprietary.protocol.impl.ViaVersionProvider
import gg.scala.cougar.spigot.proprietary.provider.PlayerPriorityProvider
import gg.scala.cougar.spigot.proprietary.provider.impl.LemonPlayerPriorityProvider
import gg.scala.cougar.spigot.proprietary.server.ServerDetailsProvider
import gg.scala.cougar.spigot.proprietary.server.impl.DefaultServerDetailsProvider
import gg.scala.cougar.spigot.proprietary.server.impl.LemonServerDetailsProvider
import gg.scala.flavor.Flavor
import gg.scala.flavor.FlavorOptions
import me.lucko.helper.plugin.ExtendedJavaPlugin
import org.bukkit.Bukkit

/**
 * @author GrowlyX
 * @since 1/12/2022
 */
class ScalaCougarSpigotProprietaryPlugin : ExtendedJavaPlugin()
{
    lateinit var priorityProvider: PlayerPriorityProvider
    lateinit var protocolVersionProvider: ProtocolVersionProvider
    lateinit var serverDetailsProvider: ServerDetailsProvider

    override fun enable()
    {
        val flavor = Flavor.create<ScalaCougarSpigotProprietaryPlugin>(
            FlavorOptions(logger)
        )

        ScalaCougarSpigotProprietaryEngine.configure()

        flavor.bind<ScalaCougarSpigotProprietaryPlugin>() to this
        flavor.startup()

        registerProviders()

        BackboneService.configure()
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
        ScalaCougarSpigotProprietaryEngine.close()
    }
}
