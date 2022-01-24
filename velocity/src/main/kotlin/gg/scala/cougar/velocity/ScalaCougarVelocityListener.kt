package gg.scala.cougar.velocity

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import gg.scala.banana.Banana
import gg.scala.banana.credentials.BananaCredentials
import gg.scala.banana.options.BananaOptions
import gg.scala.cougar.common.message.ScCougarServerMessageProducer.Companion.produce
import gg.scala.store.velocity.ScalaDataStoreVelocity

/**
 * @author GrowlyX
 * @since 1/15/2022
 */
object ScalaCougarVelocityListener
{
    private val banana = Banana(
        BananaCredentials(
            address = ScalaDataStoreVelocity.INSTANCE.redis.hostname,
            port = ScalaDataStoreVelocity.INSTANCE.redis.port,
            password = ScalaDataStoreVelocity.INSTANCE.redis.password ?: ""
        ),
        BananaOptions(
            channel = "cougar:backbone"
        )
    )

    @Subscribe
    fun onLogout(event: DisconnectEvent)
    {
        produce(
            "queue-remove",
            "uniqueId" to event.player.uniqueId
        ).dispatch(banana)
    }
}
