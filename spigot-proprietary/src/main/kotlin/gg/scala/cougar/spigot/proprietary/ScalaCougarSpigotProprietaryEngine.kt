package gg.scala.cougar.spigot.proprietary

import gg.scala.cougar.common.ScCougarPlatform
import gg.scala.cougar.common.instance.ScCougarInstance
import gg.scala.cougar.spigot.proprietary.caching.UuidCacheProvider
import gg.scala.cougar.spigot.proprietary.caching.impl.CubedUuidCacheProvider
import gg.scala.cougar.spigot.proprietary.caching.impl.DefaultUuidCacheProvider
import gg.scala.flavor.inject.Inject
import gg.scala.store.storage.impl.RedisDataStoreStorageLayer
import gg.scala.store.storage.type.DataStoreStorageType
import org.bukkit.Bukkit
import java.util.*

/**
 * @author GrowlyX
 * @since 1/12/2022
 */
object ScalaCougarSpigotProprietaryEngine : ScCougarPlatform()
{
    @Inject
    lateinit var plugin: ScalaCougarSpigotProprietaryPlugin

    private lateinit var instance: ScCougarInstance
    private lateinit var uuidCacheProvider: UuidCacheProvider

    override fun configure()
    {
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

        uuidCacheProvider = if (Bukkit.getPluginManager().getPlugin("Cubed") != null)
            CubedUuidCacheProvider() else DefaultUuidCacheProvider
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

    override fun getUuidCache() = uuidCacheProvider.getUuidCache()
}
