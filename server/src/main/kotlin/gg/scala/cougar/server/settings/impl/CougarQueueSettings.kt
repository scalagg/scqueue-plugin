package gg.scala.cougar.server.settings.impl

import gg.scala.cougar.common.queue.impl.PrimaryCougarQueue
import gg.scala.cougar.server.settings.CougarSettingsModel

/**
 * @author GrowlyX
 * @since 1/12/2022
 */
class CougarQueueSettings(
    val queues: List<PrimaryCougarQueue> = mutableListOf(
        PrimaryCougarQueue("something", "Something Queue", listOf(5, 47))
    )
) : CougarSettingsModel()
{
    override fun getAbstractType() = this::class.java
}
