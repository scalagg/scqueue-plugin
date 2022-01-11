package gg.scala.cougar.common.queue.impl

import gg.scala.cougar.common.ScCougarPlatform
import gg.scala.cougar.common.player.ScCougarPlayer
import gg.scala.flavor.inject.Inject
import java.util.*

/**
 * @author GrowlyX
 * @since 12/24/2021
 */
object PrimaryCougarQueueComparator : Comparator<UUID>
{
    @Inject
    lateinit var platform: ScCougarPlatform

    override fun compare(
        first: UUID, second: UUID
    ): Int
    {
        return try
        {
            val firstCougar = platform.playerLayer[first]!!
            val secondCougar = platform.playerLayer[second]!!

            firstCougar.priority
                .compareTo(secondCougar.priority)
        } catch (exception: Exception)
        {
            -1
        }
    }
}
