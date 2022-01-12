package gg.scala.cougar.common.instance

import gg.scala.store.storage.storable.IDataStoreObject
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * @author GrowlyX
 * @since 12/24/2021
 */
class ScCougarInstance(
    val serverId: String,
    override val identifier: UUID = UUID.randomUUID()
): IDataStoreObject
{
    companion object
    {
        @JvmStatic
        val TIMEOUT = TimeUnit.SECONDS.toMillis(10L)
    }

    val isMaxCapacity: Boolean
        get() = players >= maxPlayers

    val isNotUpToDate: Boolean
        get() = lastHeartbeat + TIMEOUT < System.currentTimeMillis()

    var players = 0
    var maxPlayers = 0

    var whitelisted = false
    var whitelistedPlayers = mutableListOf<UUID>()

    var lastHeartbeat = 0L
}
