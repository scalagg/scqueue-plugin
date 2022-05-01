package gg.scala.cougar.spigot

import gg.scala.cougar.common.ScCougarPlatform
import gg.scala.cougar.common.instance.ScCougarInstance
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.flavor.service.ignore.IgnoreAutoScan
import gg.scala.store.storage.impl.RedisDataStoreStorageLayer
import gg.scala.store.storage.type.DataStoreStorageType
import org.bukkit.Bukkit
import java.util.*

/**
 * @author GrowlyX
 * @since 1/12/2022
 */
@Service
@IgnoreAutoScan
object ScalaCougarSpigotEngine : ScCougarPlatform()
{
    @Inject
    lateinit var plugin: ScalaCougarSpigotPlugin

    private lateinit var instance: ScCougarInstance

    @Configure
    override fun configure()
    {
        super.setupLayers()
        super.configure()

        val databaseId = instanceLayer
            .useLayerWithReturn<RedisDataStoreStorageLayer<ScCougarInstance>, UUID>(
                DataStoreStorageType.REDIS
            ) {
                this.loadAllWithFilterSync {
                    it.serverId == plugin.serverDetailsProvider
                        .getServerName()
                }.values.firstOrNull()?.identifier
                    ?: UUID.randomUUID()
            }

        instance = ScCougarInstance(
            plugin.serverDetailsProvider
                .getServerName(), databaseId
        )
    }

    override fun configureFlavor(): Boolean
    {
        return false
    }

    override fun updateLocalInstance()
    {
        instance.whitelistedPlayers = Bukkit
            .getWhitelistedPlayers()
            .map { it.uniqueId }
            .toMutableList()

        instance.whitelisted = Bukkit.hasWhitelist()
        instance.maxPlayers = Bukkit.getMaxPlayers()
        instance.players = Bukkit.getOnlinePlayers().size
    }

    override fun getLocalInstance() = instance
    override fun shouldTrackInstance() = true
}
