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
    val internalQueue: LinkedList<UUID>,
    val allowedProtocols: List<Int> = mutableListOf(47),
    override val identifier: UUID = UUID.randomUUID(),
) : IDataStoreObject
{
    fun isInQueue(t: UUID) = internalQueue.contains(t)
    fun sizeOfQueue() = internalQueue.size

    fun getPosition(t: UUID): Int
    {
        val copyOfQueue = LinkedList(internalQueue)

        for (index in 0..internalQueue.size)
        {
            if (copyOfQueue.poll() == t)
            {
                return index + 1
            }
        }

        return -1
    }
}
