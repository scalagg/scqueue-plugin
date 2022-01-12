package gg.scala.cougar.common.queue

import gg.scala.cougar.common.ScCougarPlatform
import gg.scala.cougar.common.queue.impl.wrapper.CougarQueueWrapper
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * Wraps a java [PriorityQueue].
 *
 * This queue requires a [Comparator] to be able to correctly
 * priorities the objects within the [priorityQueue].
 *
 * @author GrowlyX
 * @since 12/24/2021
 */
open class Queue<T>(comparator: Comparator<T>)
{
    @Transient
    private val priorityQueue = PriorityQueue(comparator)

    fun getHandle() = priorityQueue

    fun isInQueue(t: T) = priorityQueue.contains(t)
    fun sizeOfQueue() = priorityQueue.size

    fun getPosition(t: T): Int
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
