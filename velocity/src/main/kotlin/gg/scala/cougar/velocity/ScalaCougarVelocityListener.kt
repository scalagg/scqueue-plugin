package gg.scala.cougar.velocity

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import gg.scala.cougar.common.message.ScCougarServerMessageProducer

/**
 * @author GrowlyX
 * @since 1/15/2022
 */
object ScalaCougarVelocityListener
{
    @Subscribe
    fun onLogout(event: DisconnectEvent)
    {
        ScCougarServerMessageProducer.produce(
            "queue-remove",
            "uniqueId" to event.player.uniqueId
        ).dispatch(
            ScCougarServerMessageProducer.banana
        )
    }
}
