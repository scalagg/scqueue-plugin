package gg.scala.cougar.server.iterator

import gg.scala.cougar.common.instance.ScCougarInstance
import gg.scala.cougar.common.message.ScCougarServerMessageProducer
import gg.scala.cougar.server.ScCougarServerBootstrap
import gg.scala.cougar.server.settings.impl.CougarIterationSettings
import gg.scala.cougar.server.settings.impl.CougarQueueSettings
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.flavor.service.ignore.IgnoreAutoScan
import gg.scala.store.storage.impl.RedisDataStoreStorageLayer
import gg.scala.store.storage.type.DataStoreStorageType
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * @author GrowlyX
 * @since 1/12/2022
 */
@Service("Queue Iterator")
object ScCougarQueueIterator : Runnable
{
    @Inject
    lateinit var settings: CougarQueueSettings

    @Inject
    lateinit var iteration: CougarIterationSettings

    @Configure
    fun configure()
    {
        val executor = Executors.newSingleThreadScheduledExecutor()

        executor.scheduleAtFixedRate(
            this, 500L,
            iteration.iterationDelay,
            TimeUnit.MILLISECONDS
        )
    }

    override fun run()
    {
        try
        {
            tick()
        } catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    private fun tick()
    {
        for (queue in settings.queues)
        {
            if (queue.getHandle().isEmpty())
                continue

            val instance = ScCougarServerBootstrap.instanceLayer
                .useLayerWithReturn<RedisDataStoreStorageLayer<ScCougarInstance>, CompletableFuture<ScCougarInstance?>>(
                    DataStoreStorageType.REDIS
                ) {
                    this.loadWithFilter { it.serverId.equals(queue.targetServerId, true) }
                }
                .join()

            if (
                instance == null ||
                instance.isNotUpToDate ||
                instance.isMaxCapacity
            )
                continue

            val priorityQueue = PriorityQueue(queue.getHandle())

            if (instance.whitelisted)
            {
                priorityQueue.removeIf {
                    !instance.whitelistedPlayers.contains(it)
                }
            }

            if (priorityQueue.isNotEmpty())
            {
                val uniqueId = priorityQueue.poll()

                queue.getHandle().remove(uniqueId)
                queue.pushDataUpdates().join()

                ScCougarServerMessageProducer.produce(
                    "send-player",
                    "uniqueId" to uniqueId,
                    "serverId" to queue.targetServerId
                ).dispatch(
                    "cougar:bukkit",
                    ScCougarServerMessageProducer.banana
                )
            }
        }
    }
}
