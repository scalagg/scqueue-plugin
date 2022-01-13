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
    val linkedList: LinkedList<UUID>,
    override val identifier: UUID = UUID.randomUUID(),
) : IDataStoreObject
{
    fun isInQueue(t: UUID) = linkedList.contains(t)
    fun sizeOfQueue() = linkedList.size

    fun getPosition(t: UUID): Int
    {
        val copyOfQueue = PriorityQueue(linkedList)

        for (index in 0..linkedList.size)
        {
            if (copyOfQueue.poll() == t)
            {
                return index + 1
            }
        }

        return -1
    }
}
