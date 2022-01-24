package gg.scala.cougar.common.message

import com.google.common.util.concurrent.ThreadFactoryBuilder
import gg.scala.banana.Banana
import gg.scala.banana.credentials.BananaCredentials
import gg.scala.banana.message.Message
import gg.scala.banana.options.BananaOptions
import net.evilblock.cubed.serializers.Serializers
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

/**
 * @author GrowlyX
 * @since 1/12/2022
 */
class ScCougarServerMessageProducer(
    conf: BananaCredentials
)
{
    val executor: ScheduledExecutorService = Executors
        .newSingleThreadScheduledExecutor(
            ThreadFactoryBuilder()
                .setNameFormat("Queue Messaging #%d")
                .build()
        )

    val banana = Banana(
        conf, BananaOptions(
            channel = "cougar:backbone",
            gson = Serializers.gson,
            executor = executor
        )
    )

    companion object
    {
        @JvmStatic
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
}
