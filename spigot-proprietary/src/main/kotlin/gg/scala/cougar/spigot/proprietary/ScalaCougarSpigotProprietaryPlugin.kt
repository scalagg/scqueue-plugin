package gg.scala.cougar.spigot.proprietary

import gg.scala.cougar.spigot.proprietary.backbone.BackboneService
import gg.scala.cougar.spigot.proprietary.protocol.ProtocolVersionProvider
import gg.scala.cougar.spigot.proprietary.protocol.impl.CubedVersionProvider
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
        if (Bukkit.getPluginManager().getPlugin("Lemon") != null)
        {
            priorityProvider = LemonPlayerPriorityProvider()
            serverDetailsProvider = LemonServerDetailsProvider()
        } else
        {
            serverDetailsProvider = DefaultServerDetailsProvider
                .also { it.configure(this) }
        }

        if (Bukkit.getPluginManager().getPlugin("Cubed") != null)
        {
            protocolVersionProvider = CubedVersionProvider()
        }

        if (Bukkit.getPluginManager().getPlugin("ViaVersion") != null)
        {
            protocolVersionProvider = ViaVersionProvider()
        }
    }

    override fun disable()
    {
        ScalaCougarSpigotProprietaryEngine.close()
    }
}
