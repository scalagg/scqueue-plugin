package gg.scala.cougar.velocity

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import com.velocitypowered.api.event.connection.LoginEvent
import gg.scala.aware.AwareBuilder
import gg.scala.aware.codec.codecs.interpretation.AwareMessageCodec
import gg.scala.aware.message.AwareMessage

/**
 * @author GrowlyX
 * @since 1/15/2022
 */
object ScalaCougarVelocityListener
{
    private val aware = AwareBuilder
        .of<AwareMessage>("cougar:backbone")
        .codec(AwareMessageCodec)
        .build()

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

    @JvmStatic
    fun produce(
        packet: String,
        vararg arguments: Pair<String, Any>
    ): AwareMessage
    {
        val message = AwareMessage
            .of(packet, this.aware)

        for (argument in arguments)
        {
            message.assign(
                argument.first, argument.second
            )
        }

        return message
    }
}

inline fun <reified T> contextual(
    noinline lambda: () -> Unit
)
{
    val classLoader = Thread.currentThread().contextClassLoader
    Thread.currentThread().contextClassLoader = T::class.java.classLoader

    runCatching(lambda)
        .onFailure {
            it.printStackTrace()
        }
        .getOrNull()

    Thread.currentThread().contextClassLoader = classLoader
}
