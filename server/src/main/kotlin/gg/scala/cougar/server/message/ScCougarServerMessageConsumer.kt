package gg.scala.cougar.server.message

import com.google.common.util.concurrent.ThreadFactoryBuilder
import gg.scala.banana.Banana
import gg.scala.banana.credentials.BananaCredentials
import gg.scala.banana.message.Message
import gg.scala.banana.options.BananaOptions
import gg.scala.banana.subscribe.marker.BananaHandler
import gg.scala.cougar.server.ScCougarServerBootstrap
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.flavor.service.ignore.IgnoreAutoScan
import net.evilblock.cubed.serializers.Serializers
import java.util.concurrent.Executors

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

        ScCougarServerBootstrap.LOGGER.info("Now consuming messages!")
    }
}
