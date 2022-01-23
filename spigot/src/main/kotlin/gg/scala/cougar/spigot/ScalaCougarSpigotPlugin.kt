package gg.scala.cougar.spigot

import gg.scala.banana.credentials.BananaCredentials
import gg.scala.cougar.common.message.ScCougarServerMessageProducer
import gg.scala.cougar.spigot.backbone.BackboneService
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
import gg.scala.store.spigot.ScalaDataStoreSpigot
import gg.scala.store.spigot.ScalaDataStoreSpigotImpl
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

        ScCougarServerMessageProducer.bananaConf = BananaCredentials(
            address = ScalaDataStoreSpigot.INSTANCE.redis.hostname,
            port = ScalaDataStoreSpigot.INSTANCE.redis.port,
            password = ScalaDataStoreSpigot.INSTANCE.redis.password ?: ""
        )

        registerProviders()

        ScalaCougarSpigotEngine.flavor.bind<ScalaCougarSpigotPlugin>() to this

        ScalaCougarSpigotEngine.flavor.inject(ScalaCougarSpigotEngine)
        ScalaCougarSpigotEngine.flavor.inject(BackboneService)
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
