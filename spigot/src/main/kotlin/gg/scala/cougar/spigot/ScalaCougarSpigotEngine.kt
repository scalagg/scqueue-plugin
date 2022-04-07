package gg.scala.cougar.spigot

import gg.scala.banana.credentials.BananaCredentials
import gg.scala.cougar.common.ScCougarPlatform
import gg.scala.cougar.common.instance.ScCougarInstance
import gg.scala.cougar.common.queue.impl.wrapper.CougarQueueWrapper
import gg.scala.cougar.spigot.backbone.BackboneService.platform
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Service
import gg.scala.flavor.service.ignore.IgnoreAutoScan
import gg.scala.store.storage.impl.CachedDataStoreStorageLayer
import gg.scala.store.storage.impl.RedisDataStoreStorageLayer
import gg.scala.store.storage.type.DataStoreStorageType
import org.bukkit.Bukkit
import java.util.*
import java.util.concurrent.ConcurrentHashMap

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

    override fun configure(credentials: BananaCredentials)
    {
        super.setupLayers()
        super.configure(credentials)

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
