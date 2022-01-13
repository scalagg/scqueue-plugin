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

}
