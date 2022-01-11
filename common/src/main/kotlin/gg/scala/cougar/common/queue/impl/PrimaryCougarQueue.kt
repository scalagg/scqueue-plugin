package gg.scala.cougar.common.queue.impl

import gg.scala.cougar.common.ScCougarPlatform
import gg.scala.cougar.common.queue.Queue
import gg.scala.flavor.inject.Inject
import java.util.*

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
}
