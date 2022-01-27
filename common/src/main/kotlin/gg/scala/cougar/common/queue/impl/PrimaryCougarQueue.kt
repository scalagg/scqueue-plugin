package gg.scala.cougar.common.queue.impl

import gg.scala.cougar.common.ScCougarPlatform
import gg.scala.cougar.common.message.ScCougarServerMessageProducer.Companion.produce

import gg.scala.cougar.common.queue.Queue
import gg.scala.cougar.common.queue.impl.wrapper.CougarQueueWrapper
import gg.scala.flavor.inject.Inject
import gg.scala.store.storage.type.DataStoreStorageType
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * @author GrowlyX
 * @since 12/24/2021
 */
class PrimaryCougarQueue(
    val targetServerId: String,
    val fancyDisplayName: String,
    val allowedProtocols: List<Int> =
        mutableListOf(47)
) : Queue<UUID>(
    PrimaryCougarQueueComparator
)
{
    fun pushDataUpdates(): CompletableFuture<Void>
    {
        val wrapped = CougarQueueWrapper(
            targetServerId, fancyDisplayName, LinkedList<UUID>().also { it.addAll(getHandle()) },
            allowedProtocols, ScCougarPlatform.INSTANCE.queueDatabaseIds[targetServerId]!!
        )

        return ScCougarPlatform.INSTANCE.queueLayer
            .save(wrapped, DataStoreStorageType.REDIS)
            .thenRun {
                produce(
                    "queue-update",
                    "uniqueId" to wrapped.identifier
                ).dispatch(
                    ScCougarPlatform.INSTANCE.producer.banana
                )
            }
    }
}
