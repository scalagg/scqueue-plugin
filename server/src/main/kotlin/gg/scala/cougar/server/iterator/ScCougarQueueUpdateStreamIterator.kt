package gg.scala.cougar.server.iterator

import gg.scala.cougar.common.instance.ScCougarInstance
import gg.scala.cougar.common.message.ScCougarServerMessageProducer
import gg.scala.cougar.server.ScCougarServerBootstrap
import gg.scala.cougar.server.settings.impl.CougarLanguageSettings
import gg.scala.cougar.server.settings.impl.CougarQueueSettings
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.flavor.service.ignore.IgnoreAutoScan
import gg.scala.store.storage.impl.RedisDataStoreStorageLayer
import gg.scala.store.storage.type.DataStoreStorageType
import net.evilblock.cubed.serializers.Serializers
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * @author GrowlyX
 * @since 1/12/2022
 */
@Service("Queue Update Stream")
object ScCougarQueueUpdateStreamIterator : Runnable
{
    @Inject
    lateinit var language: CougarLanguageSettings

    @Inject
    lateinit var queues: CougarQueueSettings

    @Configure
    fun configure()
    {
        val executor = Executors.newSingleThreadScheduledExecutor()

        executor.scheduleAtFixedRate(
            this, 500L,
            ScCougarQueueIterator.iteration.updateStreamIterationDelay,
            TimeUnit.MILLISECONDS
        )
    }

    private fun tick()
    {
        for (queue in queues.queues)
        {
            val instance = ScCougarServerBootstrap.instanceLayer
                .useLayerWithReturn<RedisDataStoreStorageLayer<ScCougarInstance>, CompletableFuture<ScCougarInstance?>>(
                    DataStoreStorageType.REDIS
                ) {
                    this.loadWithFilter { it.serverId.equals(queue.targetServerId, true) }
                }.join()

            val messageMap = mutableMapOf<UUID, String>()

            val offline = instance == null || instance.isNotUpToDate
            val whitelisted = instance != null && instance.whitelisted

            for (player in queue.getHandle())
            {
                var baseMessage = language.updateMessage
                    .replace("{pos}", queue.getPosition(player).toString())
                    .replace("{max}", queue.sizeOfQueue().toString())
                    .replace("{queue}", queue.fancyDisplayName)

                if (offline)
                {
                    baseMessage += "\n${language.offlineAddition}"
                } else if (whitelisted)
                {
                    baseMessage += "\n${language.whitelistedAddition}"
                }

                messageMap[player] = "$baseMessage\n "
            }

            val serializedMessageMap = Serializers
                .gson.toJson(messageMap)

            ScCougarServerMessageProducer.produce(
                "queue-update-stream",
                "queueId" to queue.targetServerId,
                "messages" to serializedMessageMap
            ).dispatch(
                "cougar:bukkit",
                ScCougarServerMessageProducer.banana
            )
        }
    }

    override fun run()
    {
        try
        {
            tick()
        } catch (e: Exception)
        {
            e.printStackTrace()
        }
    }
}
