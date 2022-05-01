package gg.scala.cougar.common.player

import gg.scala.cougar.common.ScCougarPlatform
import gg.scala.flavor.inject.Inject
import gg.scala.store.storage.storable.IDataStoreObject
import gg.scala.store.storage.type.DataStoreStorageType
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * @author GrowlyX
 * @since 12/24/2021
 */
class ScCougarPlayer(
    val uniqueId: UUID
): IDataStoreObject
{
    @Transient
    @Inject
    lateinit var platform: ScCougarPlatform

    var priority = 1

    override val identifier: UUID
        get() = uniqueId

    fun save(): CompletableFuture<Void>
    {
        return platform.playerLayer
            .save(this, DataStoreStorageType.REDIS)
    }
}
