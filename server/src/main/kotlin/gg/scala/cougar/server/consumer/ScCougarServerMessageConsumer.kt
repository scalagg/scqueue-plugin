package gg.scala.cougar.server.consumer

import gg.scala.banana.Banana
import gg.scala.banana.annotate.Subscribe
import gg.scala.banana.credentials.BananaCredentials
import gg.scala.banana.message.Message
import gg.scala.banana.options.BananaOptions
import gg.scala.banana.subscribe.marker.BananaHandler
import gg.scala.cougar.common.ScCougarPlatform
import gg.scala.cougar.common.message.ScCougarServerMessageProducer
import gg.scala.cougar.server.ScCougarServerBootstrap
import gg.scala.cougar.server.settings.CougarSettingsProcessor
import gg.scala.cougar.server.settings.impl.CougarLanguageSettings
import gg.scala.cougar.server.settings.impl.CougarQueueSettings
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.flavor.service.ignore.IgnoreAutoScan
import gg.scala.store.storage.type.DataStoreStorageType
import net.evilblock.cubed.serializers.Serializers
import java.util.*

/**
 * @author GrowlyX
 * @since 1/12/2022
 */
@IgnoreAutoScan
@Service("Message Consumer")
object ScCougarServerMessageConsumer : BananaHandler
{
    @Configure
    fun configure()
    {
        val banana = Banana(
            BananaCredentials(),
            BananaOptions(
                channel = "cougar:backbone",
                gson = Serializers.gson,
                executor = ScCougarServerMessageProducer.executor
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
            .localCache[uniqueId] = cougarPlayer
    }

    @Subscribe("player-disconnect")
    fun onPlayerDisconnect(message: Message)
    {
        val uniqueId = UUID.fromString(message["uniqueId"])
            ?: return

        ScCougarServerBootstrap.playerLayer
            .remove(uniqueId)
    }

    @Subscribe("queue-add")
    fun onQueueRemove(message: Message)
    {
        val queues = CougarSettingsProcessor
            .locate<CougarQueueSettings>()!!

        val uniqueId = UUID
            .fromString(message["uniqueId"])
            ?: return

        val queue = queues.queues
            .firstOrNull {
                it.getHandle().contains(uniqueId)
            } ?: return

        queue.getHandle().remove(uniqueId)
        queue.pushDataUpdates()
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
