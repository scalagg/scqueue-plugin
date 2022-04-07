package gg.scala.cougar.common.message

import com.google.common.util.concurrent.ThreadFactoryBuilder
import gg.scala.aware.AwareBuilder
import gg.scala.aware.codec.codecs.interpretation.AwareMessageCodec
import gg.scala.aware.message.AwareMessage
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

/**
 * @author GrowlyX
 * @since 1/12/2022
 */
object ScCougarServerMessageProducer
{
    val executor: ScheduledExecutorService = Executors
        .newSingleThreadScheduledExecutor(
            ThreadFactoryBuilder()
                .setNameFormat("Queue Messaging #%d")
                .build()
        )

    val aware = AwareBuilder
        .of<AwareMessage>("cougar:backbone")
        .codec(AwareMessageCodec)
        .build()

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
