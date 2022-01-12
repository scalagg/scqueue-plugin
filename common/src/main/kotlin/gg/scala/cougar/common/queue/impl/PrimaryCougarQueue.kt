package gg.scala.cougar.common.queue.impl

import gg.scala.cougar.common.ScCougarPlatform
import gg.scala.cougar.common.queue.Queue
import gg.scala.cougar.common.queue.impl.wrapper.CougarQueueWrapper
import gg.scala.flavor.inject.Inject
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * @author GrowlyX
 * @since 12/24/2021
 */
class PrimaryCougarQueue(
    val targetServerId: String,
    val fancyDisplayName: String
) : Queue<UUID>(
    PrimaryCougarQueueComparator
)
{
    @Inject
    @Transient
    lateinit var platform: ScCougarPlatform

    fun pushDataUpdates(): CompletableFuture<Void>
    {
        return ScCougarPlatform.INSTANCE.queueLayer.save(
            CougarQueueWrapper(targetServerId, fancyDisplayName, getHandle())
        )
    }
}
