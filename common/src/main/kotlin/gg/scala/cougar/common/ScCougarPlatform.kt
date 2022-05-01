package gg.scala.cougar.common

import com.google.common.util.concurrent.ThreadFactoryBuilder
import gg.scala.cougar.common.instance.ScCougarInstance
import gg.scala.cougar.common.message.ScCougarServerMessageProducer
import gg.scala.cougar.common.player.ScCougarPlayer
import gg.scala.cougar.common.queue.impl.wrapper.CougarQueueWrapper
import gg.scala.flavor.Flavor
import gg.scala.flavor.FlavorOptions
import gg.scala.store.controller.DataStoreObjectController
import gg.scala.store.controller.DataStoreObjectControllerCache
import gg.scala.store.storage.type.DataStoreStorageType
import java.util.*
import java.util.concurrent.*
import java.util.logging.Logger
import kotlin.properties.Delegates

/**
 * @author GrowlyX
 * @since 12/24/2021
 */
abstract class ScCougarPlatform
{
    companion object
    {
        @JvmStatic
        var INSTANCE by Delegates.notNull<ScCougarPlatform>()
    }

    var playerLayer by Delegates.notNull<DataStoreObjectController<ScCougarPlayer>>()
    var instanceLayer by Delegates.notNull<DataStoreObjectController<ScCougarInstance>>()
    var queueLayer by Delegates.notNull<DataStoreObjectController<CougarQueueWrapper>>()

    val executor: ScheduledExecutorService = Executors
        .newSingleThreadScheduledExecutor(
            ThreadFactoryBuilder()
                .setNameFormat("Queue Thread Pool #%d")
                .build()
        )

    var flavor = Flavor.create<ScCougarPlatform>(
        FlavorOptions(Logger.getLogger("ScCougarPlatform"))
    )

    val queueDatabaseIds = mutableMapOf<String, UUID>()

    abstract fun updateLocalInstance()
    abstract fun getLocalInstance(): ScCougarInstance
    abstract fun shouldTrackInstance(): Boolean

    fun loadPlayerResources(
        uniqueId: UUID
    ): CompletableFuture<ScCougarPlayer>
    {
        return playerLayer.load(uniqueId, DataStoreStorageType.REDIS)
            .thenApply {
                if (it == null)
                {
                    val injected = flavor
                        .injected<ScCougarPlayer>(uniqueId)
                    playerLayer.localCache()[uniqueId] = injected

                    return@thenApply injected
                }

                flavor.inject(it)
                playerLayer.localCache()[uniqueId] = it

                return@thenApply it
            }
    }

    fun removeResources(uniqueId: UUID)
    {
        playerLayer.delete(uniqueId, DataStoreStorageType.CACHE).join()
        playerLayer.delete(uniqueId, DataStoreStorageType.REDIS).join()
    }

    fun setupLayers()
    {
        playerLayer = DataStoreObjectControllerCache.create()
        instanceLayer = DataStoreObjectControllerCache.create()
        queueLayer = DataStoreObjectControllerCache.create()
    }

    var producer by Delegates.notNull<ScCougarServerMessageProducer>()

    fun preLoad()
    {
        INSTANCE = this
    }

    open fun configure()
    {
        flavor.bind<ScCougarPlatform>() to this

        flavor.bind<DataStoreObjectController<ScCougarPlayer>>() to playerLayer
        flavor.bind<DataStoreObjectController<ScCougarInstance>>() to instanceLayer

        producer = ScCougarServerMessageProducer
        producer.aware.listen("queue-update") {
            val uniqueId = this
                .retrieve<UUID>("uniqueId")

            queueLayer
                .load(uniqueId, DataStoreStorageType.REDIS)
                .join()?.let { wrapped ->
                    queueLayer.save(wrapped, DataStoreStorageType.CACHE).join()
                }
        }

        producer.executor.scheduleAtFixedRate({
            val queues = queueLayer
                .loadAll(DataStoreStorageType.REDIS)
                .join()

            for (queue in queues)
            {
                queueLayer.localCache()[queue.key] = queue.value
            }
        }, 1L, 1L, TimeUnit.SECONDS)

        if (shouldTrackInstance())
        {
            producer.executor.scheduleAtFixedRate({
                updateLocalInstance()

                instanceLayer.save(
                    getLocalInstance(), DataStoreStorageType.REDIS
                ).thenRun {
                    getLocalInstance().lastHeartbeat =
                        System.currentTimeMillis()
                }
            }, 1L, 1L, TimeUnit.SECONDS)
        }

        flavor.startup()

        producer.aware.connect()
    }

    fun close()
    {
        flavor.close()

        producer.executor
            .shutdownNow()

        producer.aware
            .shutdown()

        if (shouldTrackInstance())
        {
            instanceLayer.delete(
                getLocalInstance().identifier,
                DataStoreStorageType.REDIS
            ).join()
        }
    }
}
