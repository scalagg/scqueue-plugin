package gg.scala.cougar.common

import com.google.common.util.concurrent.ThreadFactoryBuilder
import gg.scala.cougar.common.instance.ScCougarInstance
import gg.scala.cougar.common.player.ScCougarPlayer
import gg.scala.flavor.Flavor
import gg.scala.flavor.FlavorOptions
import gg.scala.store.controller.DataStoreObjectController
import gg.scala.store.controller.DataStoreObjectControllerCache
import gg.scala.store.storage.type.DataStoreStorageType
import net.evilblock.cubed.store.uuidcache.impl.RedisUUIDCache
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.logging.Logger
import kotlin.properties.Delegates

/**
 * @author GrowlyX
 * @since 12/24/2021
 */
abstract class ScCougarPlatform
{
    var playerLayer by Delegates.notNull<DataStoreObjectController<ScCougarPlayer>>()
    var instanceLayer by Delegates.notNull<DataStoreObjectController<ScCougarInstance>>()

    val executor = Executors.newScheduledThreadPool(2,
        ThreadFactoryBuilder()
            .setNameFormat("Queue Thread Pool #%d")
            .setDaemon(true)
            .build()
    )

    val flavor by lazy {
        Flavor.create<ScCougarPlatform>(
            FlavorOptions(Logger.getLogger("ScCougarPlatform"))
        )
    }

    abstract fun updateLocalInstance()
    abstract fun getLocalInstance(): ScCougarInstance
    abstract fun shouldTrackInstance(): Boolean

    abstract fun getUuidCache(): RedisUUIDCache

    fun loadPlayerResources(
        uniqueId: UUID, cache: Boolean = true
    ): CompletableFuture<ScCougarPlayer>
    {
        return playerLayer.loadAndCache(uniqueId, {
            flavor.injected(uniqueId)
        }, DataStoreStorageType.REDIS)
    }

    fun configure()
    {
        flavor.bind<ScCougarPlatform>() to this

        playerLayer = DataStoreObjectControllerCache.create()
        instanceLayer = DataStoreObjectControllerCache.create()

        flavor.bind<DataStoreObjectController<ScCougarPlayer>>() to playerLayer
        flavor.bind<DataStoreObjectController<ScCougarInstance>>() to instanceLayer

        executor.scheduleAtFixedRate({
            updateLocalInstance()

            instanceLayer.save(
                getLocalInstance(), DataStoreStorageType.REDIS
            ).thenRun {
                getLocalInstance().lastHeartbeat =
                    System.currentTimeMillis()
            }
        }, 0L, 1L, TimeUnit.SECONDS)

        flavor.startup()
    }

    fun close()
    {
        flavor.close()
    }
}
