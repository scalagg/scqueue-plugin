package gg.scala.cougar.server.retention

import gg.scala.aware.message.AwareMessage
import gg.scala.cougar.common.ScCougarPlatform
import gg.scala.cougar.server.consumer.ScCougarServerMessageConsumer
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Service
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * @author GrowlyX
 * @since 2/20/2022
 */
@Service
object QueuePlayerRetention
{
    @Inject
    lateinit var platform: ScCougarPlatform

    fun retain(
        uniqueId: UUID, duration: Long, message: AwareMessage
    )
    {
        platform.executor.scheduleAtFixedRate(
            RetentionRunnable(uniqueId, message),
            0L, duration, TimeUnit.MILLISECONDS
        )
    }

    class RetentionRunnable(
        private val uniqueId: UUID,
        private val data: AwareMessage
    ) : Runnable
    {
        override fun run()
        {
            // checking if the player has
            // reconnected- we won't be
            // removing them in this case.
            val player = platform.playerLayer
                .localCache()[uniqueId]

            if (player != null)
                return

            ScCougarServerMessageConsumer
                .onQueueRemove(data)
        }
    }
}
