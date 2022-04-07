package gg.scala.cougar.common

import com.google.common.util.concurrent.ThreadFactoryBuilder
import gg.scala.banana.annotate.Subscribe
import gg.scala.banana.credentials.BananaCredentials
import gg.scala.banana.message.Message
import gg.scala.banana.subscribe.marker.BananaHandler
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
        return playerLayer.loadAndCache(uniqueId, {
            flavor.injected(uniqueId)
        }, DataStoreStorageType.REDIS)
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

    open fun configure(credentials: BananaCredentials)
    {
        flavor.bind<ScCougarPlatform>() to this

        flavor.bind<DataStoreObjectController<ScCougarPlayer>>() to playerLayer
        flavor.bind<DataStoreObjectController<ScCougarInstance>>() to instanceLayer

        producer = ScCougarServerMessageProducer(credentials)

        producer.banana.registerClass(object : BananaHandler
        {
            @Subscribe("queue-update")
            fun onQueueUpdate(message: Message)
            {
                message["uniqueId"]?.let {
                    val uniqueId = UUID.fromString(it)

                    queueLayer
                        .load(uniqueId, DataStoreStorageType.REDIS)
                        .join()?.let { wrapped ->
                            queueLayer.save(wrapped, DataStoreStorageType.CACHE).join()
                        }
                }
            }
        })

        producer.banana.subscribe()

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
    }

    fun close()
    {
        flavor.close()

        if (shouldTrackInstance())
        {
            instanceLayer.delete(
                getLocalInstance().identifier,
                DataStoreStorageType.REDIS
            ).join()
        }
    }
}
