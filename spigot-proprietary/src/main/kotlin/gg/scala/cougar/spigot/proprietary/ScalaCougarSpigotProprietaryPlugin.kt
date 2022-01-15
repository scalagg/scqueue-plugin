package gg.scala.cougar.spigot.proprietary

import gg.scala.cougar.spigot.proprietary.backbone.BackboneService
import gg.scala.cougar.spigot.proprietary.protocol.ProtocolVersionProvider
import gg.scala.cougar.spigot.proprietary.protocol.impl.CubedVersionProvider
import gg.scala.cougar.spigot.proprietary.provider.PlayerPriorityProvider
import gg.scala.cougar.spigot.proprietary.provider.impl.LemonPlayerPriorityProvider
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

    override fun enable()
    {
        val flavor = Flavor.create<ScalaCougarSpigotProprietaryPlugin>(
            FlavorOptions(logger)
        )

        ScalaCougarSpigotProprietaryEngine.configure()

        flavor.bind<ScalaCougarSpigotProprietaryPlugin>() to this
        flavor.startup()

        if (Bukkit.getPluginManager().getPlugin("Lemon") != null)
        {
            priorityProvider = LemonPlayerPriorityProvider()
        }

        if (Bukkit.getPluginManager().getPlugin("Cubed") != null)
        {
            protocolVersionProvider = CubedVersionProvider()
        }

        BackboneService.configure()
    }

    override fun disable()
    {
        ScalaCougarSpigotProprietaryEngine.close()
    }
}
