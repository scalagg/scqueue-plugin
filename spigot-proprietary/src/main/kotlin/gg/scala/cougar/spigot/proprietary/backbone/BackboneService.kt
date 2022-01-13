package gg.scala.cougar.spigot.proprietary.backbone

import gg.scala.banana.Banana
import gg.scala.banana.credentials.BananaCredentials
import gg.scala.banana.options.BananaOptions
import gg.scala.cougar.common.message.ScCougarServerMessageProducer
import gg.scala.flavor.service.Service
import net.evilblock.cubed.serializers.Serializers

/**
 * @author GrowlyX
 * @since 1/12/2022
 */
@Service
object BackboneService
{
    val banana = Banana(
        BananaCredentials(),
        BananaOptions(
            channel = "cougar:backbone",
            gson = Serializers.gson,
            executor = ScCougarServerMessageProducer.executor
        )
    )
}
