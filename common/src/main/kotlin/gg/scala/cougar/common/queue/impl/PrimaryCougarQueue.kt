package gg.scala.cougar.common.queue.impl

import gg.scala.aware.thread.AwareThreadContext
import gg.scala.commons.agnostic.sync.server.ServerContainer
import gg.scala.commons.agnostic.sync.server.impl.GameServer
import gg.scala.cougar.common.ScCougarPlatform
import gg.scala.cougar.common.message.ScCougarServerMessageProducer.produce

import gg.scala.cougar.common.queue.Queue
import gg.scala.cougar.common.queue.impl.wrapper.CougarQueueWrapper
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
    private val allowedProtocols: List<Int> =
        mutableListOf(47),
    val aggregated: Boolean = false,
    val aggregatedGroup: String = "duels"
) : Queue<UUID>(
    PrimaryCougarQueueComparator
)
{
    fun getGameServer() = if (aggregated)
    {
        ServerContainer
            .getServersInGroup(aggregatedGroup)
            .filterIsInstance<GameServer>()
            .sortedBy { it.getPlayersCount() }
            .firstOrNull()
    } else
    {
        ServerContainer.getServer<GameServer>(targetServerId)
    }

    fun pushDataUpdates(): CompletableFuture<Void>
    {
        val wrapped = CougarQueueWrapper(
            targetServerId, fancyDisplayName, LinkedList(getHandle()),
            allowedProtocols, ScCougarPlatform.INSTANCE.queueDatabaseIds[targetServerId]!!
        )

        return ScCougarPlatform.INSTANCE.queueLayer
            .save(wrapped, DataStoreStorageType.REDIS)
            .thenRun {
                produce(
                    "queue-update",
                    "uniqueId" to wrapped.identifier
                ).publish(
                    AwareThreadContext.SYNC
                )
            }
    }
}
