package gg.scala.cougar.common.message

import gg.scala.banana.Banana
import gg.scala.banana.credentials.BananaCredentials
import gg.scala.banana.options.BananaOptions
import gg.scala.banana.subscribe.marker.BananaHandler
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.flavor.service.ignore.IgnoreAutoScan
import net.evilblock.cubed.serializers.Serializers

/**
 * @author GrowlyX
 * @since 1/12/2022
 */
@Service
@IgnoreAutoScan
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
}
