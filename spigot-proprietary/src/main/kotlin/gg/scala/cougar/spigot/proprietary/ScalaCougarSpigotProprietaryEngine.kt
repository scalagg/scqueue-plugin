package gg.scala.cougar.spigot.proprietary

import gg.scala.cougar.common.ScCougarPlatform
import gg.scala.cougar.common.instance.ScCougarInstance
import gg.scala.lemon.Lemon
import gg.scala.store.storage.impl.RedisDataStoreStorageLayer
import gg.scala.store.storage.type.DataStoreStorageType
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.store.uuidcache.impl.RedisUUIDCache
import org.bukkit.Bukkit
import java.util.*

/**
 * @author GrowlyX
 * @since 1/12/2022
 */
object ScalaCougarSpigotProprietaryEngine : ScCougarPlatform()
{
    private lateinit var instance: ScCougarInstance

    override fun configure()
    {
        val databaseId = instanceLayer
            .useLayerWithReturn<RedisDataStoreStorageLayer<ScCougarInstance>, UUID>(
                DataStoreStorageType.REDIS
            ) {
                this.loadAllWithFilterSync {
                    it.serverId == Lemon.instance.settings.id
                }.values.firstOrNull()?.identifier
                    ?: UUID.randomUUID()
            }

        instance = ScCougarInstance(
            Lemon.instance.settings.id, databaseId
        )

        super.configure()
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

    override fun getUuidCache() =
        Cubed.instance.uuidCache as RedisUUIDCache
}
