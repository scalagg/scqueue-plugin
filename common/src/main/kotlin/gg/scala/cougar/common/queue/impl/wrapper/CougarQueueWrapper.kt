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
    val players: PriorityQueue<UUID>,
    override val identifier: UUID = UUID.randomUUID(),
) : IDataStoreObject
