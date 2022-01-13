package gg.scala.cougar.common.queue.impl.wrapper

import gg.scala.store.storage.storable.IDataStoreObject
import java.util.*

/**
 * @author GrowlyX
 * @since 1/12/2022
 */
class CougarQueueWrapper(
    val serverId: String,
    val fancyDisplayName: String,
    private val priorityQueue: PriorityQueue<UUID>,
    override val identifier: UUID = UUID.randomUUID(),
) : IDataStoreObject
{
    fun isInQueue(t: UUID) = priorityQueue.contains(t)
    fun sizeOfQueue() = priorityQueue.size

    fun getPosition(t: UUID): Int
    {
        val copyOfQueue = PriorityQueue(priorityQueue)

        for (index in 0..priorityQueue.size)
        {
            if (copyOfQueue.poll() == t)
            {
                return index + 1
            }
        }

        return -1
    }
}
