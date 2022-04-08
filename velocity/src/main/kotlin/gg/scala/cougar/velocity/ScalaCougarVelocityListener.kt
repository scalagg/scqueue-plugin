package gg.scala.cougar.velocity

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import com.velocitypowered.api.event.connection.LoginEvent
import gg.scala.aware.AwareHub
import gg.scala.aware.uri.WrappedAwareUri
import gg.scala.cougar.common.message.ScCougarServerMessageProducer.produce
import net.evilblock.cubed.serializers.Serializers

/**
 * @author GrowlyX
 * @since 1/15/2022
 */
object ScalaCougarVelocityListener
{
    init
    {
        AwareHub.configure(
            WrappedAwareUri()
        ) {
            Serializers.gson
        }
    }

    @Subscribe
    fun onLogout(event: LoginEvent)
    {
        produce(
            "friend-network-update",
            "uniqueId" to event.player.uniqueId,
            "type" to "joined",
        ).apply {
            publish(channel = "friends")
        }
    }

    @Subscribe
    fun onLogout(event: DisconnectEvent)
    {
        produce(
            "network-disconnect",
            "uniqueId" to event.player.uniqueId
        ).apply {
            publish(channel = "party:backbone")
        }

        produce(
            "friend-network-update",
            "uniqueId" to event.player.uniqueId,
            "type" to "left",
        ).apply {
            publish(channel = "friends")
        }
    }
}
