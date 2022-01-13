package gg.scala.cougar.common.message

import com.google.common.util.concurrent.ThreadFactoryBuilder
import gg.scala.banana.Banana
import gg.scala.banana.credentials.BananaCredentials
import gg.scala.banana.message.Message
import gg.scala.banana.options.BananaOptions
import net.evilblock.cubed.serializers.Serializers
import java.util.concurrent.Executors

/**
 * @author GrowlyX
 * @since 1/12/2022
 */
object ScCougarServerMessageProducer
{
    val executor = Executors.newScheduledThreadPool(2,
        ThreadFactoryBuilder()
            .setNameFormat("Queue Messaging #%d")
            .build()
    )

    val banana = Banana(
        BananaCredentials(),
        BananaOptions(
            channel = "cougar:backbone",
            gson = Serializers.gson,
            executor = executor
        )
    )

    fun produce(
        packet: String,
        vararg arguments: Pair<String, Any>
    ): Message
    {
        val message = Message(packet)

        for (argument in arguments)
        {
            message[argument.first] = argument
                .second.toString()
        }

        return message
    }
}
