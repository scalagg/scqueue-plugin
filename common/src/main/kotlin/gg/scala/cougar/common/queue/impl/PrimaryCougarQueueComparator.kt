package gg.scala.cougar.common.queue.impl

import gg.scala.cougar.common.ScCougarPlatform
import gg.scala.cougar.common.player.ScCougarPlayer
import gg.scala.flavor.inject.Inject
import gg.scala.store.storage.impl.CachedDataStoreStorageLayer
import gg.scala.store.storage.type.DataStoreStorageType
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * @author GrowlyX
 * @since 12/24/2021
 */
object PrimaryCougarQueueComparator : Comparator<UUID>
{
    @Inject
    lateinit var platform: ScCougarPlatform

    private val players: ConcurrentHashMap<UUID, ScCougarPlayer>
        get() = platform.playerLayer.useLayerWithReturn<CachedDataStoreStorageLayer<ScCougarPlayer>, ConcurrentHashMap<UUID, ScCougarPlayer>>(
            DataStoreStorageType.CACHE
        ) {
            this.connection.getConnection()
        }

    override fun compare(
        first: UUID, second: UUID
    ): Int
    {
        return try
        {
            val firstCougar = players[first]!!
            val secondCougar = players[second]!!

            firstCougar.priority
                .compareTo(secondCougar.priority)
        } catch (exception: Exception)
        {
            -1
        }
    }
}
