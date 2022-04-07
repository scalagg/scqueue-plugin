package gg.scala.cougar.server.consumer

import gg.scala.banana.Banana
import gg.scala.banana.annotate.Subscribe
import gg.scala.banana.message.Message
import gg.scala.banana.options.BananaOptions
import gg.scala.banana.subscribe.marker.BananaHandler
import gg.scala.cougar.common.ScCougarPlatform
import gg.scala.cougar.server.ScCougarServerBootstrap
import gg.scala.cougar.server.redis.ScCougarRedisEngine
import gg.scala.cougar.server.retention.QueuePlayerRetention
import gg.scala.cougar.server.settings.CougarSettingsProcessor
import gg.scala.cougar.server.settings.impl.CougarPrioritySettings
import gg.scala.cougar.server.settings.impl.CougarQueueSettings
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.store.storage.type.DataStoreStorageType
import net.evilblock.cubed.serializers.Serializers
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * @author GrowlyX
 * @since 1/12/2022
 */
@Service("Message Consumer")
object ScCougarServerMessageConsumer : BananaHandler
{
    @Inject
    lateinit var platform: ScCougarPlatform

    @Inject
    lateinit var priority: CougarPrioritySettings

    @Configure
    fun configure()
    {
        val banana = Banana(
            ScCougarRedisEngine.bananaWrapper,
            BananaOptions(
                channel = "cougar:backbone",
                gson = Serializers.gson,
                executor = platform.producer.executor
            )
        )

        banana.registerClass(this)
        banana.subscribe()
    }

    @Subscribe("player-connect")
    fun onPlayerConnect(message: Message)
    {
        val uniqueId = UUID.fromString(message["uniqueId"])
            ?: return

        val cougarPlayer = ScCougarPlatform.INSTANCE.playerLayer
            .load(uniqueId, DataStoreStorageType.REDIS).join()
            ?: return

        ScCougarServerBootstrap.playerLayer
            .localCache()[uniqueId] = cougarPlayer
    }

    @Subscribe("player-disconnect")
    fun onPlayerDisconnect(message: Message)
    {
        val uniqueId = UUID.fromString(message["uniqueId"])
            ?: return

        ScCougarServerBootstrap.playerLayer
            .localCache()
            .remove(uniqueId)
    }

    @Subscribe("network-disconnect")
    fun onNetworkDisconnect(message: Message)
    {
        val uniqueId = UUID
            .fromString(message["uniqueId"])
            ?: return

        val player = ScCougarServerBootstrap
            .playerLayer.localCache()[uniqueId]
            ?: return

        // checking if there's a configured
        // mapping for their priority
        val duration = priority
            .priorityToDuration[player.priority]

        if (duration == null)
        {
            onQueueRemove(message)
        } else
        {
            // scheduling a removal from the
            // queue after x amount of millis
            QueuePlayerRetention
                .retain(
                    uniqueId,
                    TimeUnit.MINUTES.toMillis(duration),
                    message
                )
        }
    }

    @Subscribe("queue-remove")
    fun onQueueRemove(message: Message)
    {
        val queues = CougarSettingsProcessor
            .locate<CougarQueueSettings>()!!

        val uniqueId = UUID
            .fromString(message["uniqueId"])
            ?: return

        val queueId = message["queueId"]

        if (queueId == null)
        {
            val queue = queues.queues
                .filter {
                    it.isInQueue(uniqueId)
                }

            for (cougarQueue in queue)
            {
                cougarQueue.getHandle().remove(uniqueId)
                cougarQueue.pushDataUpdates()
            }
        } else
        {
            val queue = queues.queues
                .firstOrNull {
                    it.targetServerId == queueId
                } ?: return

            queue.getHandle().remove(uniqueId)
            queue.pushDataUpdates()
        }
    }

    @Subscribe("queue-add")
    fun onQueueAdd(message: Message)
    {
        val queues = CougarSettingsProcessor
            .locate<CougarQueueSettings>()!!
        val serverId = message["serverId"]

        val uniqueId = UUID
            .fromString(message["uniqueId"])
            ?: return

        val queue = queues.queues.firstOrNull {
            it.targetServerId.equals(serverId, true)
        } ?: return

        if (!queue.getHandle().contains(uniqueId))
        {
            queue.getHandle().add(uniqueId)
            queue.pushDataUpdates()
        }
    }
}
